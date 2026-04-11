package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.proto.InteractionsCountRequestProto;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.practicum.ewm.stats.proto.SimilarEventsRequestProto;
import ru.practicum.ewm.stats.proto.UserPredictionsRequestProto;
import ru.practicum.model.EventSimilarity;
import ru.practicum.model.UserAction;
import ru.practicum.repository.EventSimilarityRepository;
import ru.practicum.repository.UserActionRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final EventSimilarityRepository eventSimilarityRepository;
    private final UserActionRepository userActionRepository;

    public List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request) {

        List<Long> userEventIds = userActionRepository.findByUserId(request.getUserId());

        if (userEventIds.isEmpty())
            return List.of();

        List<EventSimilarity> similarities = eventSimilarityRepository.findPotentialRecommendations(userEventIds);

        // Сначала вычисляем оценки для всех кандидатов
        Map<Long, Double> eventRelevanceScores = estimateEventRecommendationScores(similarities, userEventIds);

        return eventRelevanceScores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(entry -> RecommendedEventProto.newBuilder()
                        .setEventId(entry.getKey())
                        .setScore(entry.getValue())
                        .build())
                .limit(request.getMaxResult())
                .collect(Collectors.toList());
    }

    public List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {

        List<EventSimilarity> similarities = eventSimilarityRepository.findAllByEventIdOrderedByScoreDesc(
                request.getEventId());
        // Получаем ID событий, с которыми пользователь уже взаимодействовал (исключая текущее)
        List<Long> eventsIds = userActionRepository.findByUserIdExcludeEventId(request.getUserId(),
                request.getEventId());

        return similarities.stream()
                // Фильтр исключаем события, которые пользователь уже видел
                .filter(s -> !eventsIds.contains(s.getEventA()) && !eventsIds.contains(s.getEventB()))
                // Сортируем по убыванию сходства
                .sorted(Comparator.comparing(EventSimilarity::getSimilarity, Comparator.reverseOrder()))
                // Преобразуем в RecommendedEventProto
                .map(similarity -> {
                    long recommended = (similarity.getEventA() == request.getEventId())
                            ? similarity.getEventB()
                            : similarity.getEventA();
                    return RecommendedEventProto.newBuilder()
                            .setEventId(recommended)
                            .setScore(similarity.getSimilarity())
                            .build();
                })
                // Ограничиваем результат
                .limit(request.getMaxResult())
                .collect(Collectors.toList());

    }

    public List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {
        List<UserAction> userActions = userActionRepository.findByEventIdIn(
                new HashSet<>(request.getEventIdList())
        );

        Map<Long, Double> aggregatedWeights = userActions.stream()
                .collect(Collectors.groupingBy(
                        UserAction::getEventId,
                        Collectors.summingDouble(UserAction::getRating)
                ));

        return aggregatedWeights.entrySet().stream()
                .map(entry -> RecommendedEventProto.newBuilder()
                        .setEventId(entry.getKey())
                        .setScore(entry.getValue())
                        .build()
                )
                .collect(Collectors.toList());
    }



    private double computeEventRelevance(
            List<EventSimilarity> similarityPairs,
            List<UserAction> userEngagements,
            long targetEventId
    ) {
        // Создаём карту весов событий на основе действий пользователя
        Map<Long, Double> eventWeights = userEngagements.stream()
                .collect(Collectors.toMap(
                        UserAction::getEventId,
                        UserAction::getRating,
                        (existing, replacement) -> replacement // стратегия разрешения коллизий
                ));

        double totalWeightedRelevance = 0.0;
        double totalSimilarityScore = 0.0;

        for (EventSimilarity connection : similarityPairs) {
            // Определяем ID соседнего события
            long neighbourEventId = (connection.getEventA() == targetEventId)
                    ? connection.getEventB()
                    : connection.getEventA();

            // Получаем вес события из истории пользователя (0, если не найдено)
            double userEngagementWeight = eventWeights.getOrDefault(neighbourEventId, 0.0);
            // Берём оценку сходства из текущей пары
            double pairSimilarity = connection.getSimilarity();
            // Вычисляем взвешенную релевантность для этой связи
            double weightedRelevance = userEngagementWeight * pairSimilarity;

            totalWeightedRelevance += weightedRelevance;
            totalSimilarityScore += pairSimilarity;
        }

        // Возвращаем средневзвешенную релевантность (избегаем деления на ноль)
        return totalSimilarityScore == 0 ? 0.0 : totalWeightedRelevance / totalSimilarityScore;
    }

    private Map<Long, Double> estimateEventRecommendationScores(
            List<EventSimilarity> eventConnections,
            List<Long> userHistory
    ) {
        Map<Long, Double> finalScores = new HashMap<>();

        for (EventSimilarity connection : eventConnections) {
            // Определяем кандидата для рекомендации: то событие из пары, которого нет в истории пользователя
            long recommendationCandidate = userHistory.contains(connection.getEventA())
                    ? connection.getEventB()
                    : connection.getEventA();

            // Получаем топ‑5 наиболее похожих событий для кандидата
            List<EventSimilarity> topConnections = eventSimilarityRepository.findNeighbours(
                    recommendationCandidate,
                    PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "score"))
                    );

            // Извлекаем ID соседних событий (исключая самого кандидата)
            List<Long> neighbourIds = topConnections.stream()
                    .map(neighbour -> (neighbour.getEventA() == recommendationCandidate)
                            ? neighbour.getEventB()
                            : neighbour.getEventA())
                    .toList();

            // Получаем действия пользователя по соседним событиям
            List<UserAction> userInteractions = userActionRepository.findByEventIdIn(
                    new HashSet<>(neighbourIds)
            );

            // Вычисляем итоговую оценку для кандидата
            double calculatedRelevance = computeEventRelevance(
                    topConnections,
                    userInteractions,
                    recommendationCandidate
            );

            finalScores.put(recommendationCandidate, calculatedRelevance);
        }

        return finalScores;
    }
}
