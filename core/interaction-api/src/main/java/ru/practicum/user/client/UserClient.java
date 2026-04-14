package ru.practicum.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "user-service", path = "api/v1/users")
public interface UserClient {


    @GetMapping("/{id}")
    Optional<UserDto> getUserById(@PathVariable Long id);

}
