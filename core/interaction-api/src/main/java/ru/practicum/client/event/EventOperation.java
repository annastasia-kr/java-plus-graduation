package ru.practicum.client.event;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.dto.EventDto;

import java.util.Optional;

public interface EventOperation {

    @GetMapping("/api/v1/events/{id}")
    Optional<EventDto> getEvent(@PathVariable @NotNull Long id);

}
