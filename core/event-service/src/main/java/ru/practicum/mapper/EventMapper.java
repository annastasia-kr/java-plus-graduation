package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;
import ru.practicum.event.dto.EventDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.enums.StateEvent;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.user.dto.UserDto;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
        imports = {LocalDateTime.class, StateEvent.class},
        uses = {CategoryMapper.class, LocationMapper.class})
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(source = "category", target = "category")
    @Mapping(source = "initiator.id", target = "initiator")
    @Mapping(source = "location", target = "location")
    @Mapping(target = "createdOn", expression = "java(LocalDateTime.now())")
    @Mapping(target = "confirmedRequests", constant = "0L")
    @Mapping(target = "state", constant = "PENDING")
    @Mapping(target = "participantLimit", expression = "java(newEventDto.getParticipantLimit() == null ? 0L : newEventDto.getParticipantLimit())")
    @Mapping(target = "requestModeration", expression = "java(newEventDto.getRequestModeration() == null ? true : newEventDto.getRequestModeration())")
    Event toEvent(NewEventDto newEventDto, Category category, UserDto initiator, Location location);

    @Mapping(source = "event.category", target = "category")
    @Mapping(source = "event.location", target = "location")
    @Mapping(source = "confirmedRequests", target = "confirmedRequests")
    @Mapping(source = "views", target = "views")
    @Mapping(source = "event.initiator", target = "initiator")
    EventDto toEventDto(Event event, Long confirmedRequests, Long views);

    @Mapping(source = "category", target = "category")
    @Mapping(source = "confirmedRequests", target = "confirmedRequests")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "annotation", target = "annotation")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "eventDate", target = "eventDate")
    @Mapping(source = "paid", target = "paid")
    @Mapping(source = "participantLimit", target = "participantLimit")
    @Mapping(target = "views", constant = "0L")
    @Mapping(source = "event.initiator", target = "initiator")
    EventShortDto toEventShortDto(Event event);

}
