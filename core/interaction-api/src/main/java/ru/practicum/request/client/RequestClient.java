package ru.practicum.request.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventResult;
import ru.practicum.request.dto.EventRequestStatusUpdateDto;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.enums.RequestStatus;

import java.util.List;

@FeignClient(name = "request-service", path = "api/v1/requests")
public interface RequestClient {

    @GetMapping("/{id}")
    List<RequestDto> findAllByEventId(@PathVariable Long id);

    @PatchMapping("/user/{userId}/event/{eventId}/status")
    EventRequestStatusUpdateResult updateRequestStatus(@PathVariable Long userId,
                                                       @PathVariable Long eventId,
                                                       @RequestBody EventRequestStatusUpdateDto request);

    @GetMapping("/confirmed/count")
    List<EventResult> countRequestsForEvents(@RequestParam("eventIds") List<Long> eventIds,
                                             @RequestParam RequestStatus requestStatus);
}

