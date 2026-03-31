package ru.practicum.request.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.event.dto.EventResult;
import ru.practicum.request.dto.EventRequestStatusUpdateDto;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.enums.RequestStatus;

import java.util.List;

@Slf4j
@Component
public class RequestClientFallback implements RequestClient {

    @Override
    public long countByEventIdAndStatus(Long eventId, RequestStatus status) {
        log.error("Failed to get event count");
        return 0;
    }

    @Override
    public List<RequestDto> getRequestsByEventId(Long eventId) {
        log.error("Failed to get requests by event id");

        return List.of();
    }

    @Override
    public List<EventResult> countByEventIdsAndStatus(List<Long> eventIds, RequestStatus status) {
        return List.of();
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateDto eventRequestStatusUpdateDto) {
        return null;
    }


}
