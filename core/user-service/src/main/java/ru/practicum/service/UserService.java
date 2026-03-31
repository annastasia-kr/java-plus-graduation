package ru.practicum.service;

import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(NewUserRequest newUserRequest);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    List<UserDto> getUsers(Integer from, Integer size);

    List<UserDto> getUsers(List<Long> ids);

    void deleteUser(Long userId);

}
