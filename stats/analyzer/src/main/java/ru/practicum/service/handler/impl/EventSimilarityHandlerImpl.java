package ru.practicum.service.handler.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.service.EventSimilarityService;
import ru.practicum.service.handler.EventSimilarityHandler;

import static ru.practicum.mapper.EventSimilarityMapper.toEventSimilarity;

@Component
@RequiredArgsConstructor
public class EventSimilarityHandlerImpl implements EventSimilarityHandler {

    private final EventSimilarityService service;

    @Override
    public void handle(EventSimilarityAvro eventSimilarityAvro) {
        service.addNewAction(toEventSimilarity(eventSimilarityAvro));
    }
}
