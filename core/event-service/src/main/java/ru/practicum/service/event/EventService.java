package ru.practicum.service.event;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.dto.*;
import ru.practicum.enums.Sort;
import ru.practicum.enums.StateEvent;
import ru.practicum.dto.EventRequestStatusUpdateDto;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.RequestDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventService {

    Collection<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size);

    EventDto createEvent(Long userId, NewEventDto newEventDto);

    EventDto getEventById(Long userId, Long eventId);

    EventDto updateEventByUser(Long userId, Long eventId, UpdateEventDtoUserRequest updateEventDtoUserRequest);

    Collection<RequestDto> getRequestsByUserIdAndEventId(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateDto eventRequestStatusUpdateDto);

    Collection<EventDto> getEventsByAdmin(List<Long> users, List<StateEvent> states, List<Long> categories, LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd, Integer from, Integer size);

    EventDto updateEventByAdmin(Long eventId, UpdateEventDtoAdminRequest updateEventDtoAdminRequest);

    EventDto getEvent(Long id, HttpServletRequest httpServletRequest);

    EventDto getEvent(Long id);

    Collection<EventDto> getEventsPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Boolean onlyAvailable, Sort sort, Integer from,
                                         Integer size, HttpServletRequest httpServletRequest);
}
