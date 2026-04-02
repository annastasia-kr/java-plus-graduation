package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ru.practicum.model.User;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    User toUser(NewUserRequest newUserRequest);

    UserDto toUserDto(User user);

    UserShortDto toUserShortDto(User user);

    @Mapping(target = "email", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    User toUser(UserShortDto dto);
}