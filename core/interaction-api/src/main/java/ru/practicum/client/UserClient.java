package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.UserDto;

import java.util.List;

@FeignClient(name = "user-service", path = "/api/v1/users", fallback = UserClientFallback.class)
public interface UserClient {
    @GetMapping
    List<UserDto> getUsers(
            @RequestParam List<Long> ids);
}
