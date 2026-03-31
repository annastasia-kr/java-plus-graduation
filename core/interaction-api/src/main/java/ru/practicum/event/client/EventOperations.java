package ru.practicum.event.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.event.dto.EventDto;

import java.util.Optional;

@FeignClient(name = "event-client", path = "/api/v1/events")
public interface EventOperations {

    @GetMapping("/{id}")
    EventDto getEvent(@PathVariable Long id);

}
