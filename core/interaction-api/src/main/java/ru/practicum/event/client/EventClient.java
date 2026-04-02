package ru.practicum.event.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.event.dto.EventDto;

import java.util.List;

@FeignClient(name = "event-service")
public interface EventClient {

    List<EventDto> getEvents(List<Long> eventId);
}
