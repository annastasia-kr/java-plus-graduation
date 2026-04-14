package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventSimilarityService {

    private static final Map<ActionTypeAvro, Double> ACTION_WEIGHTS = Map.of(
            ActionTypeAvro.VIEW, 0.4,
            ActionTypeAvro.REGISTER, 0.8,
            ActionTypeAvro.LIKE, 1.0
    );

    private final Map<Long, Map<Long, Double>> userEventWeights = new HashMap<>();
    private final Map<Long, Double> eventTotalWeights = new HashMap<>();
    private final Map<Long, Map<Long, Double>> minWeightSums = new HashMap<>();

    public List<EventSimilarityAvro> updateSimilarity(UserActionAvro userActionAvro) {
        long eventId = userActionAvro.getEventId();
        long userId = userActionAvro.getUserId();
        double newWeight = ACTION_WEIGHTS.get(userActionAvro.getActionType());

        Map<Long, Double> userWeightsForEvent = userEventWeights.computeIfAbsent(
                eventId, k -> new HashMap<>()
        );
        double oldWeight = userWeightsForEvent.getOrDefault(userId, 0.0);

        if (newWeight <= oldWeight) {
            return Collections.emptyList();
        }

        userWeightsForEvent.merge(userId, newWeight, Math::max);

        double oldSum = eventTotalWeights.getOrDefault(eventId, 0.0);
        double newSum = oldSum - oldWeight + newWeight;
        eventTotalWeights.put(eventId, newSum);

        List<EventSimilarityAvro> updatedSimilarities = new ArrayList<>();

        for (long otherEventId : userEventWeights.keySet()) {
            if (otherEventId == eventId || !userEventWeights.get(otherEventId).containsKey(userId) ||
                    userEventWeights.get(otherEventId) == null) continue;

            double updatedSimilarity = recalculateSimilarityForPair(
                    eventId, otherEventId, userId, oldWeight, newWeight);
            updatedSimilarities.add(
                    new EventSimilarityAvro(
                            Math.min(eventId, otherEventId),
                            Math.max(eventId, otherEventId),
                            updatedSimilarity,
                            userActionAvro.getTimestamp()
                    )
            );
        }
        return updatedSimilarities;
    }

    private double recalculateSimilarityForPair(long eventA, long eventB, long userId, double oldWeight, double newWeight) {
        Map<Long, Double> weightsForEventB = userEventWeights.getOrDefault(eventB, new HashMap<>());
        double weightB = weightsForEventB.getOrDefault(userId, 0.0);

        if (weightB == 0.0) {
            return calculateCurrentSimilarity(eventA, eventB);
        }

        double oldContribution = Math.min(oldWeight, weightB);
        double newContribution = Math.min(newWeight, weightB);
        double delta = newContribution - oldContribution;
        updateMinWeightSum(eventA, eventB, delta);
        return calculateCurrentSimilarity(eventA, eventB);
    }

    private void updateMinWeightSum(long eventA, long eventB, double delta) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);

        minWeightSums
                .computeIfAbsent(first, x -> new HashMap<>())
                .merge(second, delta, Double::sum);
    }

    private double calculateCurrentSimilarity(long eventA, long eventB) {
        double minSum = getMinWeightSum(eventA, eventB);
        double totalA = eventTotalWeights.getOrDefault(eventA, 0.0);
        double totalB = eventTotalWeights.getOrDefault(eventB, 0.0);

        if (totalA == 0 || totalB == 0) return 0.0;

        double denominator = Math.sqrt(totalA) * Math.sqrt(totalB);
        if (denominator == 0) return 0.0;

        return minSum / denominator;
    }

    private double getMinWeightSum(long eventA, long eventB) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);
        return minWeightSums.getOrDefault(first, new HashMap<>())
                .getOrDefault(second, 0.0);
    }
}