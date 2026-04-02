package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.dto.EventDto;

import java.util.Optional;

@FeignClient(name = "event-service")
public interface EventClient {

    @GetMapping("/api/v1/events/{id}")
    Optional<EventDto> getEvent(@PathVariable Long id);
}
