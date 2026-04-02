package ru.practicum.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.enums.StateEvent;
import ru.practicum.event.dto.UpdateEventDtoAdminRequest;
import ru.practicum.event.dto.EventDto;
import ru.practicum.service.event.EventService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
@Slf4j
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<EventDto> getEventsByAdmin(@RequestParam(required = false) List<Long> users,
                                                 @RequestParam(required = false) List<StateEvent> states,
                                                 @RequestParam(required = false) List<Long> categories,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {

        log.trace("AdminEventController getEventsByAdmin");
        return eventService.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto updateEvent(@PathVariable Long eventId,
                                @Valid @RequestBody UpdateEventDtoAdminRequest updateEventDtoAdminRequest) {

        log.trace("AdminEventController updateEvent {} {}", eventId, updateEventDtoAdminRequest);
        return eventService.updateEventByAdmin(eventId, updateEventDtoAdminRequest);
    }
}
