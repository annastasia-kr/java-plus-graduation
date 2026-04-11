package ru.practicum.service.handler.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.config.KafkaConfig;
import ru.practicum.ewm.stats.proto.UserActionProto;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.service.UserActionProducer;
import ru.practicum.service.handler.UserActionHandler;

import static ru.practicum.mapper.AvroMapper.toUserActionAvro;

@Service
@RequiredArgsConstructor
public class UserActionHandlerImpl implements UserActionHandler {

    private final UserActionProducer producer;

    @Override
    public void handle(UserActionProto userActionProto) {
        UserActionAvro userActionAvro = toUserActionAvro(userActionProto);

        producer.send(userActionAvro.getTimestamp(), KafkaConfig.KafkaTopic.USER_ACTIONS, userActionAvro,
                userActionAvro.getUserId());
    }
}
