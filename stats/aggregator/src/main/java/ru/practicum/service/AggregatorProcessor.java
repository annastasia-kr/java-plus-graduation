package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.config.KafkaConfig;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class AggregatorProcessor {

    private static final Duration TIMEOUT = Duration.ofMillis(5000);

    private final KafkaConsumer<Long, UserActionAvro> consumer;
    private final KafkaProducer<Long, SpecificRecordBase> producer;
    private final String userActionTopic;
    private final String eventSimilarityTopic;
    private final EventSimilarityService eventSimilarityService;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    public AggregatorProcessor(KafkaConfig config, EventSimilarityService eventSimilarityService) {
        this.producer = new KafkaProducer<>(config.getProducerProperties());
        this.consumer = new KafkaConsumer<>(config.getConsumerProperties());
        this.userActionTopic = config.getTopics().get(KafkaConfig.KafkaTopic.USER_ACTIONS);
        this.eventSimilarityTopic = config.getTopics().get(KafkaConfig.KafkaTopic.EVENTS_SIMILARITY);
        this.eventSimilarityService = eventSimilarityService;
    }

    public void start() {
        try {
            // ... подписка на топик ...
            consumer.subscribe(List.of(this.userActionTopic));

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                consumer.wakeup();
            }));
            // Цикл обработки событий
            while (true) {

                int count = 0;

                ConsumerRecords<Long, UserActionAvro> records = consumer.poll(TIMEOUT);

                if (records.isEmpty()) {
                    log.debug("No new records received after {} ms", TIMEOUT);
                    Thread.sleep(100);
                    continue;
                }

                // ... реализация цикла опроса ...
                for (ConsumerRecord<Long, UserActionAvro> record : records) {
                    try {
                        List<EventSimilarityAvro> eventSimilarityAvros = eventSimilarityService.updateSimilarity(record.value());
                        for (EventSimilarityAvro eventSimilarity : eventSimilarityAvros) {
                            ProducerRecord<Long, SpecificRecordBase> producerRecord = buildProducerRecord(eventSimilarity);
                            producer.send(producerRecord);
                            manageOffsets(record, count++, consumer);
                            log.info("Similarity between events ID {} and {} has been sent to topic {}",
                                    eventSimilarity.getEventA(), eventSimilarity.getEventB(), producerRecord.topic());
                        }
                    } catch (Exception e) {
                        log.error("Error processing record", e);
                    }
                }
            }

        } catch (WakeupException ignored) {
            log.info("Consumer wakeup triggered, initiating shutdown...");
        } catch (Exception e) {
            log.error("Unexpected error in consumer loop: {}", e.getMessage(), e);
        } finally {
            shutdown();
        }
    }
    private void shutdown() {

        try {
            producer.flush();
            log.info("Producer buffer flushed successfully");

            if (!consumer.assignment().isEmpty()) {
                consumer.commitSync();
                log.info("Offsets committed successfully");
            }

        } catch (Exception e) {
            log.error("Error during shutdown: {}", e.getMessage(), e);
        } finally {

            try {
                producer.close();
                log.info("Producer closed successfully");
            } catch (Exception e) {
                log.error("Error closing producer: {}", e.getMessage(), e);
            }

            try {
                consumer.close();
                log.info("Consumer closed successfully");
            } catch (Exception e) {
                log.error("Error closing consumer: {}", e.getMessage(), e);
            }
        }
    }

    private ProducerRecord<Long, SpecificRecordBase> buildProducerRecord(EventSimilarityAvro eventSimilarityAvro) {
        return new ProducerRecord<>(
                this.eventSimilarityTopic,
                null,
                eventSimilarityAvro.getTimestamp().toEpochMilli(),
                eventSimilarityAvro.getEventA(),
                eventSimilarityAvro
        );
    }

    private void manageOffsets(ConsumerRecord<Long, UserActionAvro> record, int count,
                               KafkaConsumer<Long, UserActionAvro> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Offset commit failures: {}", offsets, exception);
                }
            });
        }
    }

}
