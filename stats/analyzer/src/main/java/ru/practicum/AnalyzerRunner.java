package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.practicum.service.EventSimilarityProcessor;
import ru.practicum.service.UserActionProcessor;

@Component
@RequiredArgsConstructor
public class AnalyzerRunner implements CommandLineRunner {

    private final EventSimilarityProcessor eventSimilarityProcessor;
    private final UserActionProcessor userActionProcessor;

    @Override
    public void run(String... args) throws Exception {
        Thread hubEventsThread = new Thread(userActionProcessor);

        hubEventsThread.setName("HubEventHandlerThread");
        hubEventsThread.start();

        eventSimilarityProcessor.start();

    }
}
