package ru.practicum.client.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.UserDto;

import java.util.List;

public interface UserOperation {

    @GetMapping("/api/v1/users")
    List<UserDto> getUsers(
            @RequestParam List<Long> ids);
}
