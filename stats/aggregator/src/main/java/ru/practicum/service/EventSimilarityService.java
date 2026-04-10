package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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

    private final ConcurrentHashMap<Long, ConcurrentHashMap<Long, Double>> userEventWeights = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Double> eventTotalWeights = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, ConcurrentHashMap<Long, Double>> minWeightSums = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, ConcurrentSkipListSet<Long>> userInteractionHistory = new ConcurrentHashMap<>();

    public List<EventSimilarityAvro> updateSimilarity(UserActionAvro userActionAvro) {
        long eventId = userActionAvro.getEventId();
        long userId = userActionAvro.getUserId();
        double newWeight = ACTION_WEIGHTS.get(userActionAvro.getActionType());

        ConcurrentHashMap<Long, Double> userWeightsForEvent = userEventWeights.computeIfAbsent(
                eventId, k -> new ConcurrentHashMap<>()
        );

        Double oldWeight = userWeightsForEvent.compute(userId, (key, current) -> {
            if (current == null) return newWeight;
            return Math.max(current, newWeight);
        });

        if (oldWeight != null && newWeight <= oldWeight) {
            return Collections.emptyList();
        }

        updateEventTotalWeight(eventId, oldWeight != null ? oldWeight : 0.0, newWeight);

        ConcurrentSkipListSet<Long> userEvents = userInteractionHistory.computeIfAbsent(
                userId, k -> new ConcurrentSkipListSet<>()
        );

        List<EventSimilarityAvro> updatedSimilarities = new ArrayList<>();
        Set<Long> eventsCopy = new HashSet<>(userEvents);

        for (long otherEventId : eventsCopy) {
            if (otherEventId == eventId) continue;

            double updatedSimilarity = recalculateSimilarityForPair(
                    eventId, otherEventId, userId,
                    oldWeight != null ? oldWeight : 0.0, newWeight
            );
            updatedSimilarities.add(
                    new EventSimilarityAvro(
                            Math.min(eventId, otherEventId),
                            Math.max(eventId, otherEventId),
                            updatedSimilarity,
                            Instant.now()
                    )
            );
        }

        userEvents.add(eventId);
        return updatedSimilarities;
    }

    private void updateEventTotalWeight(long eventId, double oldWeight, double newWeight) {
        eventTotalWeights.merge(eventId, newWeight - oldWeight, Double::sum);
    }

    private double recalculateSimilarityForPair(long eventA, long eventB, long userId, double oldWeight, double newWeight) {
        ConcurrentHashMap<Long, Double> weightsForEventB = userEventWeights.getOrDefault(eventB, new ConcurrentHashMap<>());
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
                .computeIfAbsent(first, x -> new ConcurrentHashMap<>())
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
        return minWeightSums.getOrDefault(first, new ConcurrentHashMap<>())
                .getOrDefault(second, 0.0);
    }
}