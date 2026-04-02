package ru.practicum.event.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.event.dto.EventDto;

import java.util.List;

@FeignClient(name = "event-service", path = "api/v1/events")
public interface EventClient {

    @GetMapping
    List<EventDto> getEvents(@RequestParam("eventIds") List<Long> eventIds);

}
