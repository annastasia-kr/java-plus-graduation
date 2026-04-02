package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.UserDto;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("/api/v1/users")
    List<UserDto> getUsers(
            @RequestParam List<Long> ids);
}
