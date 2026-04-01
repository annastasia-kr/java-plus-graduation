package ru.practicum.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.client.EventClient;
import ru.practicum.dto.EventDto;
import ru.practicum.service.event.EventService;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Slf4j
public class InternalEventController implements EventClient {

    private final EventService eventService;

    @Override
    public Optional<EventDto> getEvent(Long id) {
        return eventService.getEvent(id);
    }

}
