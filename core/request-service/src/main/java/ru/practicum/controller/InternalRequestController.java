package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventResult;
import ru.practicum.client.RequestClient;
import ru.practicum.dto.EventRequestStatusUpdateDto;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.RequestDto;
import ru.practicum.enums.RequestStatus;
import ru.practicum.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requests")
@RequiredArgsConstructor
@Slf4j
public class InternalRequestController implements RequestClient {

    private final RequestService requestService;

    @Override
    public long countByEventIdAndStatus(Long eventId, RequestStatus status) {
        return requestService.countByEventIdAndStatus(eventId, status);
    }

    @Override
    public List<RequestDto> getRequestsByEventId(Long eventId) {
        return requestService.getRequestsByEventId(eventId);
    }

    @Override
    public List<EventResult> countByEventIdsAndStatus(List<Long> eventIds, RequestStatus status) {
        return requestService.countByEventIdsAndStatus(eventIds, status);
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                       EventRequestStatusUpdateDto eventRequestStatusUpdateDto) {
        return requestService.updateRequestStatus(userId, eventId, eventRequestStatusUpdateDto);
    }

}
