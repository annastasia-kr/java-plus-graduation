package ru.practicum.service.handler.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.service.UserActionService;
import ru.practicum.service.handler.UserActionHandler;

import static ru.practicum.mapper.UserActionMapper.toUserAction;

@Component
@RequiredArgsConstructor
public class UserActionHandlerImpl implements UserActionHandler {

    private final UserActionService service;

    @Override
    public void handle(UserActionAvro userActionAvro) {
        service.addNew(toUserAction(userActionAvro));
    }
}
