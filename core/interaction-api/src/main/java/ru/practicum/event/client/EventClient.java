package ru.practicum.event.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.event.dto.EventDto;

@FeignClient(name = "event-service", path = "/api/v1/events", fallback = EventClientFallback.class)
public interface EventClient {

    @GetMapping("/{id}")
    EventDto getEvent(@PathVariable Long id);
}
