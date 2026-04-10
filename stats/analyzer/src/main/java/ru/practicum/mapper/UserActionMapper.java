package ru.practicum.mapper;

import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.model.UserAction;

public class UserActionMapper {

    private final static double WEIGHT_OF_LIKE = 1;
    private final static double WEIGHT_OF_REGISTER = 0.8;
    private final static double WEIGHT_OF_VIEW = 0.4;

    public static final UserAction toUserAction(UserActionAvro userActionAvro) {
        return UserAction.builder()
                .userId(userActionAvro.getUserId())
                .eventId(userActionAvro.getEventId())
                .created(userActionAvro.getTimestamp())
                .rating(getWeight(userActionAvro.getActionType()))
                .build();
    }

    private static double getWeight(ActionTypeAvro actionType) {
        return switch (actionType) {
            case VIEW -> WEIGHT_OF_VIEW;
            case REGISTER -> WEIGHT_OF_REGISTER;
            case LIKE -> WEIGHT_OF_LIKE;
        };
    }
}
