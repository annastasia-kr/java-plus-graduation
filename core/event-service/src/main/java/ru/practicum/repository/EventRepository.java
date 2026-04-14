package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Event;

import java.util.List;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiator(Long userId, Pageable pageable);

    boolean existsByCategoryId(Long categoryId);

    Set<Event> findAllByIdIn(Set<Long> key);
}