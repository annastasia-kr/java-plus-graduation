package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.request.enums.RequestStatus;
import ru.practicum.event.dto.EventResult;
import ru.practicum.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByIdIn(List<Long> requestIds);

    List<Request> findAllByEventIdAndStatus(Long eventId, RequestStatus status);

    Long countByEventIdAndStatus(Long eventId, RequestStatus status);

    List<Request> findAllByRequesterId(Long requesterId);

    boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);

    @Query("SELECT COUNT(r) FROM Request r " +
            "WHERE r.eventId = :eventId AND r.status = ru.practicum.requests.enums.RequestStatus.CONFIRMED")
    Long countConfirmedRequests(@Param("eventId") Long eventId);

    @Query("SELECT r.eventId AS eventId, COUNT(r) AS count FROM Request r " +
            "WHERE r.eventId IN :eventIds " +
            "AND r.status = :status " +
            "GROUP BY r.eventId")
    List<EventResult> countByEventIdsAndStatus(
            @Param("eventIds") List<Long> eventIds,
            @Param("status") RequestStatus status
    );

    long countByEventAndStatus(Long eventId, RequestStatus status);

}
