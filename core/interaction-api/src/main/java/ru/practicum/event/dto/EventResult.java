package ru.practicum.event.dto;

import lombok.Getter;

@Getter
public class EventResult {
    private Long eventId;
    private Long count;

    // Конструктор для JPQL
    public EventResult(Long eventId, Long count) {
        this.eventId = eventId;
        this.count = count;
    }

}