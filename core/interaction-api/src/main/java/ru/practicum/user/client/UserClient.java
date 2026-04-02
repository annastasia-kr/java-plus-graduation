package ru.practicum.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.user.dto.UserDto;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("api/v1/events/{id}")
    List<UserDto> getUsers(@RequestParam List<Long> userId);
}
