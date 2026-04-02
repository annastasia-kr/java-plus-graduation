package ru.practicum.service;

import ru.practicum.request.dto.EventRequestStatusUpdateDto;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.RequestDto;

import java.util.List;
import java.util.Map;

public interface RequestService {

    List<RequestDto> getUserRequests(Long userId);

    RequestDto create(Long userId, Long eventId);

    RequestDto cancelRequest(Long userId, Long requestId);

    List<RequestDto> getRequestsByEventId(Long eventId);

    EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateDto eventRequestStatusUpdateDto);

    List<RequestDto> findAllByEventId(Long id);

    Map<Long, Long> countRequestsForEvents(List<Long> longs);
}
