package ru.practicum.event.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.event.dto.EventDto;

import java.util.Optional;

@FeignClient(name = "event-service", path = "api/v1/events")
public interface EventClient {

    @GetMapping("/{id}")
    Optional<EventDto> getEventById(@PathVariable Long id);
}
