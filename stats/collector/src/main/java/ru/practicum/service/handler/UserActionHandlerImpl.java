package ru.practicum.service.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.config.KafkaConfig;
import ru.practicum.ewm.stats.proto.UserActionProto;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.service.UserActionProducer;

import static ru.practicum.mapper.AvroMapper.toUserActionAvro;
@Component
@RequiredArgsConstructor
public class UserActionHandlerImpl implements UserActionHandler{

    private final UserActionProducer producer;

    @Override
    public void handle(UserActionProto userActionProto) {
        UserActionAvro userActionAvro = toUserActionAvro(userActionProto);

        producer.send(userActionAvro.getTimestamp(), KafkaConfig.KafkaTopic.USER_ACTIONS, userActionAvro,
                userActionAvro.getUserId());
    }
}
