package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.proto.UserActionProto;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Instant;

@Component
public class AvroMapper {

    private static final String PROTOBUF_ACTION_PREFIX = "ACTION_";

    public static UserActionAvro toUserActionAvro(UserActionProto userActionProto) {

        ActionTypeAvro actionTypeAvro = ActionTypeAvro.valueOf(
                userActionProto.getActionType().name().substring(PROTOBUF_ACTION_PREFIX.length()));

        return UserActionAvro.newBuilder()
                .setUserId(userActionProto.getUserId())
                .setEventId(userActionProto.getEventId())
                .setActionType(actionTypeAvro)
                .setTimestamp(Instant.ofEpochSecond(userActionProto.getTimestamp().getSeconds(),
                        userActionProto.getTimestamp().getNanos()))
                .build();
    }


}
