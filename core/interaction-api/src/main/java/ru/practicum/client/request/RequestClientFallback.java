package ru.practicum.client.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.dto.EventRequestStatusUpdateDto;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.EventResult;
import ru.practicum.dto.RequestDto;
import ru.practicum.enums.RequestStatus;

import java.util.List;

@Slf4j
@Component
public class RequestClientFallback implements RequestOperation {

    @Override
    public long countByEventIdAndStatus(Long eventId, RequestStatus status) {
        return 0L;
    }

    @Override
    public List<RequestDto> getRequestsByEventId(Long eventId) {
        return List.of();
    }

    @Override
    public List<EventResult> countByEventIdsAndStatus(List<Long> eventIds, RequestStatus status) {
        return List.of();
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateDto eventRequestStatusUpdateDto) {

        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = new EventRequestStatusUpdateResult();
        return eventRequestStatusUpdateResult;
    }
}
