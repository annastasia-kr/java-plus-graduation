package ru.practicum.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.EventSimilarity;

import java.util.List;
import java.util.Optional;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, Long> {
    Optional<EventSimilarity> findByEventAAndEventB(Long eventA, Long eventB);

    @Query("SELECT s FROM EventSimilarity s " +
            "WHERE s.eventA = :eventId OR s.eventB = :eventId " +
            "ORDER BY s.similarity DESC")
    List<EventSimilarity> findAllByEventIdOrderedByScoreDesc(long eventId);

    @Query("SELECT s " +
            "FROM EventSimilarity s " +
            "WHERE (s.eventA IN :eventIds OR s.eventB IN :eventIds) " +
            "AND NOT (s.eventA IN :eventIds AND s.eventB IN :eventIds)")
    List<EventSimilarity> findPotentialRecommendations(List<Long> userEventIds);

    List<EventSimilarity> findNeighbours(long recommendationCandidate, PageRequest score);
}
