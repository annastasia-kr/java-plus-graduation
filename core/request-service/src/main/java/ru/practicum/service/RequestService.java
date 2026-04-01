package ru.practicum.service;

import ru.practicum.dto.EventResult;
import ru.practicum.dto.EventRequestStatusUpdateDto;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.enums.RequestStatus;
import ru.practicum.dto.RequestDto;

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
