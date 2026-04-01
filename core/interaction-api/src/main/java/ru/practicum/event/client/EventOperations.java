package ru.practicum.event.client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.event.dto.EventDto;

public interface EventOperations {

    @GetMapping("/api/v1/events/{id}")
    EventDto getEvent(@PathVariable Long id);

}
