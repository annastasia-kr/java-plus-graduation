package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.UserAction;
import ru.practicum.repository.UserActionRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserActionService {

    private final UserActionRepository repository;

    public void addNew(UserAction userAction) {
        Optional<UserAction> existing = repository.findByUserIdAndEventId(userAction.getUserId(),
                userAction.getEventId());

        // if present - update
        existing.ifPresentOrElse(
                current -> {
                    if (userAction.getRating() > current.getRating()) {
                        current.setRating(userAction.getRating());
                        repository.save(current);
                    }
                },
                () -> repository.save(userAction)
        );
    }
}
