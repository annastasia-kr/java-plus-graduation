package ru.practicum.request.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventResult;
import ru.practicum.request.dto.EventRequestStatusUpdateDto;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.enums.RequestStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@FeignClient(name = "request-service")
public interface RequestClient {

    @GetMapping("api/v1/requests/{id}")
    List<RequestDto> findAllByEventId(@RequestParam Long id);

    @PatchMapping("api/v1/requests/user/{userId}/event/{eventId}/status")
    EventRequestStatusUpdateResult updateRequestStatus(@PathVariable Long userId,
                                                       @PathVariable Long eventId,
                                                       @RequestBody EventRequestStatusUpdateDto request);

    @GetMapping("/confirmed/count")
    List<EventResult> countRequestsForEvents(@RequestParam("eventIds") List<Long> eventIds, RequestStatus requestStatus);
}

