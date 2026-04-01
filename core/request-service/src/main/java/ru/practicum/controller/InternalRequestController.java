package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventResult;
import ru.practicum.request.client.RequestClient;
import ru.practicum.request.dto.EventRequestStatusUpdateDto;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.enums.RequestStatus;
import ru.practicum.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requests")
@RequiredArgsConstructor
@Slf4j
public class InternalRequestController implements RequestClient {

    private final RequestService requestService;

    @GetMapping("/{eventId}/count")
    public long countByEventIdAndStatus(@PathVariable Long eventId, RequestStatus status) {
        return requestService.countByEventIdAndStatus(eventId, status);
    }

    @GetMapping("/{eventId}")
    public List<RequestDto> getRequestsByEventId(@PathVariable Long eventId) {
        return requestService.getRequestsByEventId(eventId);
    }

    @GetMapping("/count")
    public List<EventResult> countByEventIdsAndStatus(@RequestParam List<Long> eventIds, RequestStatus status) {
        return requestService.countByEventIdsAndStatus(eventIds, status);
    }

    @PatchMapping("/user/{userId}/event/{eventId}")
    public EventRequestStatusUpdateResult updateRequestStatus(@PathVariable Long userId, @PathVariable Long eventId,
                                                       @RequestBody EventRequestStatusUpdateDto eventRequestStatusUpdateDto) {
        return requestService.updateRequestStatus(userId, eventId, eventRequestStatusUpdateDto);
    }

}
