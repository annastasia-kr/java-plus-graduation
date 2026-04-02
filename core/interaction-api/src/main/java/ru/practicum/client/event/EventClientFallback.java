package ru.practicum.client.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.dto.EventDto;

import java.util.Optional;

@Slf4j
@Component
public class EventClientFallback implements EventOperation {
    @Override
    public Optional<EventDto> getEvent(Long id) {
        return Optional.empty();
    }
}
