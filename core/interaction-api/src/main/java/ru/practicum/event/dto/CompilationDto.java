package ru.practicum.event.dto;

import lombok.Data;

import java.util.Collection;

@Data
public class CompilationDto {
    private Long id;
    private Boolean pinned;
    private String title;
    private Collection<EventShortDto> events;
}
