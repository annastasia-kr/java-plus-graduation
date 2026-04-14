package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.UserAction;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;


public interface UserActionRepository extends JpaRepository<UserAction, Long> {
    Optional<UserAction> findByUserIdAndEventId(Long userId, Long eventId);

    @Query("SELECT a.eventId " +
            "FROM UserAction a " +
            "WHERE a.userId = :userId " +
            "AND a.eventId != :eventId")
    List<Long> findByUserIdExcludeEventId(@Param("userId") long userId, @Param("eventId") long eventId);

    List<Long> findByUserId(long userId);

    List<UserAction> findByEventIdIn(HashSet<Long> longs);
}
