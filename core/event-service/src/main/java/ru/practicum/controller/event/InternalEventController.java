package ru.practicum.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventDto;
import ru.practicum.service.event.EventService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Slf4j
public class InternalEventController {

    private final EventService eventService;

    @GetMapping("{/id}")
    public Optional<EventDto> getEvent(@PathVariable Long id) {
        return eventService.getEvent(id);
    }


}
