package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.UserService;
import ru.practicum.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class InternalUserController {

    private final UserService userService;

    @GetMapping("/api/v1/users")
    public List<UserDto> getUsers(@RequestParam List<Long> ids) {
        log.info("GET /api/v1/users - получение списка пользователей, ids={}", ids);
        return userService.getUsers(ids);
    }
}
