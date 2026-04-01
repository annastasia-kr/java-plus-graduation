package ru.practicum.event.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.event.dto.EventDto;

@Slf4j
@Component
public class EventClientFallback implements EventClient {

    @Override
    public EventDto getEvent(Long id) {
        return EventDto.builder().build();
    }

}
