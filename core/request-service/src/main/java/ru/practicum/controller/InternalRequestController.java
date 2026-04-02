package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventResult;
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
public class InternalRequestController {

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

    @GetMapping("/{eventId}/count")
    public EventRequestStatusUpdateResult updateRequestStatus(@PathVariable Long userId, Long eventId,
                                                       EventRequestStatusUpdateDto eventRequestStatusUpdateDto) {
        return requestService.updateRequestStatus(userId, eventId, eventRequestStatusUpdateDto);
    }

}
