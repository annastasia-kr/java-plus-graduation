package ru.practicum.compilation.dto;

import lombok.Data;
import ru.practicum.event.dto.EventShortDto;

import java.util.Collection;

@Data
public class CompilationDto {
    private Long id;
    private Boolean pinned;
    private String title;
    private Collection<EventShortDto> events;
}
