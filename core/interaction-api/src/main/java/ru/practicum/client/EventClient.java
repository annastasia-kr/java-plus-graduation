package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.dto.EventDto;

import java.util.Optional;

@FeignClient(name = "event-service", path = "/api/v1/events", fallback = EventClientFallback.class)
public interface EventClient {

    @GetMapping("/{id}")
    Optional<EventDto> getEvent(@PathVariable Long id);
}
