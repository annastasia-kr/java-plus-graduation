package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.model.Request;
import ru.practicum.request.dto.RequestDto;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    @Mapping(target = "event", source = "eventId")
    @Mapping(target = "requester", source = "requesterId")
    RequestDto toRequestDto(Request request);
}