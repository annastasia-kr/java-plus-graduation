package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.event.dto.EventResult;
import ru.practicum.model.Request;
import ru.practicum.request.enums.RequestStatus;

import java.util.*;
import java.util.stream.Collectors;


public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByIdIn(List<Long> requestIds);

    List<Request> findAllByEventIdAndStatus(Long eventId, RequestStatus status);

    List<Request> findAllByRequesterId(Long requesterId);

    boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);

    @Query("SELECT COUNT(r) FROM Request r " +
            "WHERE r.eventId = :eventId AND r.status = ru.practicum.request.enums.RequestStatus.CONFIRMED")
    Long countConfirmedRequests(@Param("eventId") Long eventId);

    @Query("SELECT new ru.practicum.event.dto.EventResult(r.eventId, COUNT(r.id)) " +
            "FROM Request r " +
            "WHERE r.eventId IN :eventIds AND r.status = :status " +
            "GROUP BY r.eventId")
    List<EventResult> countByEventIdsAndStatus(@Param("eventIds") List<Long> eventIds,
                                               @Param("status") RequestStatus status);
    default Map<Long, Long> countByEventIdsAndStatusMap(List<Long> eventIds, RequestStatus status) {
        if (Objects.isNull(eventIds) || eventIds.isEmpty() || Objects.isNull(status)) {
            return Collections.emptyMap();
        }
        List<Long> uniqueEventIds = new ArrayList<>(new HashSet<>(eventIds));
        return countByEventIdsAndStatus(uniqueEventIds, status)
                .stream()
                .collect(Collectors.toMap(eventResult -> eventResult.getEventId(), eventResult -> eventResult.getCount()));
    }
}
