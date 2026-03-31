package ru.practicum.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.user.dto.UserDto;

import java.util.List;

@FeignClient(name = "user-service", path = "/api/v1/users")
public interface UserOperations {

    @GetMapping
    List<UserDto> getUsers(
            @RequestParam List<Long> ids);
}