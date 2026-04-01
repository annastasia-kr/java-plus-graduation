package ru.practicum.user.client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserOperations {

    @GetMapping("/api/v1/users")
    List<UserDto> getUsers(
            @RequestParam List<Long> ids);
}