package ru.practicum.service;

import ru.practicum.event.dto.EventResult;
import ru.practicum.request.dto.EventRequestStatusUpdateDto;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.enums.RequestStatus;
import ru.practicum.request.dto.RequestDto;

import java.util.List;

public interface RequestService {

    List<RequestDto> getUserRequests(Long userId);

    RequestDto create(Long userId, Long eventId);

    RequestDto cancelRequest(Long userId, Long requestId);

    long countByEventIdAndStatus(Long eventId, RequestStatus status);

    List<RequestDto> getRequestsByEventId(Long eventId);

    List<EventResult> countByEventIdsAndStatus(List<Long> eventIds, RequestStatus status);

    EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateDto eventRequestStatusUpdateDto);
}
