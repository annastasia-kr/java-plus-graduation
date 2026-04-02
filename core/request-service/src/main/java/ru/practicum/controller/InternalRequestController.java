package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.EventRequestStatusUpdateDto;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.service.RequestService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/requests")
@RequiredArgsConstructor
@Slf4j
public class InternalRequestController {

    private final RequestService requestService;


    @GetMapping("/{eventId}")
    public List<RequestDto> getRequestsByEventId(@PathVariable Long eventId) {
        return requestService.getRequestsByEventId(eventId);
    }

    @PatchMapping("api/v1/requests/user/{userId}/event/{eventId}/status")
    EventRequestStatusUpdateResult updateRequestStatus(@PathVariable Long userId,
                                                       @PathVariable Long eventId,
                                                       @RequestBody EventRequestStatusUpdateDto eventRequestStatusUpdateDto) {

        return requestService.updateRequestStatus(userId, eventId, eventRequestStatusUpdateDto);
    }

    @GetMapping("/confirmed/count")
    public Map<Long, Long> countByEventIdsAndStatus(@RequestParam("eventIds") List<Long> eventIds) {
        return requestService.countRequestsForEvents(eventIds);
    }
}

