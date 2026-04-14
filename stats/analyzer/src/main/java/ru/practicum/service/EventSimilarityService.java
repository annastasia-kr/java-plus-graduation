package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.EventSimilarity;
import ru.practicum.repository.EventSimilarityRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EventSimilarityService {

    private final EventSimilarityRepository eventSimilarityRepository;

    public void addNewAction(EventSimilarity eventSimilarity) {
        Optional<EventSimilarity> existing  = eventSimilarityRepository.findByEventAAndEventB(
                        eventSimilarity.getEventA(),
                        eventSimilarity.getEventB());

        // if present - update
        existing.ifPresentOrElse(
                current -> {
                    if (current.getSimilarity() != eventSimilarity.getSimilarity()) {
                        current.setSimilarity(eventSimilarity.getSimilarity());
                        eventSimilarityRepository.save(current);
                    }
                },
                () -> eventSimilarityRepository.save(eventSimilarity)
        );
    }
}
