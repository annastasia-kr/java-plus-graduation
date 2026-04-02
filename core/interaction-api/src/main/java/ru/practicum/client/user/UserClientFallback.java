package ru.practicum.client.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.dto.UserDto;

import java.util.List;

@Slf4j
@Component
public class UserClientFallback implements UserOperation {
    @Override
    public List<UserDto> getUsers(List<Long> ids) {
        return List.of();
    }
}
