package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    @Mapping(target = "pinned", defaultValue = "false")
    Compilation toCompilation(NewCompilationDto compilation);

    @Mapping(target = "events", ignore = true)
    CompilationDto toCompilationDto(Compilation compilation);

    Collection<EventShortDto> toEventShortDtoCollection(List<Event> events);
}
