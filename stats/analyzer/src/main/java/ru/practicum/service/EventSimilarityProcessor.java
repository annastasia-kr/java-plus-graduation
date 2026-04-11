package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.config.KafkaConfig;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.service.handler.EventSimilarityHandler;

import java.time.Duration;
import java.util.List;

@Component
@Slf4j
public class EventSimilarityProcessor {

    private static final Duration TIMEOUT = Duration.ofMillis(5000);

    private final KafkaConsumer<Long, EventSimilarityAvro> consumer;
    private final EventSimilarityHandler handler;
    private final String topic;

    public EventSimilarityProcessor(KafkaConfig config, EventSimilarityHandler handler) {
        this.handler = handler;
        this.consumer = new KafkaConsumer<>(config.getSimilarityProperties());
        this.topic = config.getTopics().get(KafkaConfig.KafkaTopic.EVENTS_SIMILARITY);
    }

    public void start() {
        try {
            // ... подписка на топик ...
            consumer.subscribe(List.of(this.topic));

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                consumer.wakeup();
            }));
            // Цикл обработки событий
            while (true) {

                ConsumerRecords<Long, EventSimilarityAvro> records = consumer.poll(TIMEOUT);

                if (records.isEmpty()) {
                    log.debug("No new records received after {} ms", TIMEOUT);
                    Thread.sleep(100);
                    continue;
                }

                // ... реализация цикла опроса ...
                for (ConsumerRecord<Long, EventSimilarityAvro> record : records) {
                    EventSimilarityAvro eventSimilarityAvro = record.value();

                    handler.handle(eventSimilarityAvro);
                }
            }
        } catch (WakeupException ignored) {
            log.info("Wakeup triggered, initiating shutdown...");
        } catch (Exception e) {
            log.error("Unexpected error in EventSimilarityProcessor loop: {}", e.getMessage(), e);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                log.info("Closing Event Similarity consumer");
                consumer.close();
            }
        }
    }

}
