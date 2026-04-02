package ru.practicum.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateRequestController {

    private final RequestService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getUserRequests(@PathVariable Long userId) {
        log.info("GET /users/{}/requests - получение запросов пользователя", userId);
        return service.getUserRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto create(
            @PathVariable Long userId,
            @RequestParam @NotNull Long eventId) {
        log.info("POST /users/{}/requests - создание запроса на участие в событии {}", userId, eventId);
        return service.create(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public RequestDto cancelRequest(
            @PathVariable Long userId,
            @PathVariable Long requestId) {
        log.info("PATCH /users/{}/requests/{}/cancel - отмена запроса", userId, requestId);
        return service.cancelRequest(userId, requestId);
    }
}
