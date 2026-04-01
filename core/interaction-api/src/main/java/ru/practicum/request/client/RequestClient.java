package ru.practicum.request.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.event.dto.EventResult;
import ru.practicum.request.dto.EventRequestStatusUpdateDto;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.enums.RequestStatus;

import java.util.List;

@FeignClient(name = "request-service", fallback = RequestClientFallback.class)
public interface RequestClient {

    @GetMapping("/api/v1/requests/{eventId}/count")
    long countByEventIdAndStatus(@PathVariable Long eventId, RequestStatus status);

    @GetMapping("/api/v1/requests/{eventId}")
    List<RequestDto> getRequestsByEventId(@PathVariable Long eventId);

    @GetMapping("/api/v1/requests/count")
    List<EventResult> countByEventIdsAndStatus(@RequestParam List<Long> eventIds, RequestStatus status);

    @PatchMapping("/api/v1/requests/user/{userId}/event/{eventId}")
    EventRequestStatusUpdateResult updateRequestStatus(@PathVariable Long userId, @PathVariable Long eventId,
                                                       @RequestParam EventRequestStatusUpdateDto eventRequestStatusUpdateDto);
}
