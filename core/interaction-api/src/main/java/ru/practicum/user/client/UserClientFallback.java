package ru.practicum.user.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.user.dto.UserDto;

import java.util.List;

@Slf4j
@Component
public class UserClientFallback implements UserOperations {

    @Override
    public List<UserDto> getUsers(List<Long> ids) {
        log.error("Failed to get users");

        return List.of();
    }
}
