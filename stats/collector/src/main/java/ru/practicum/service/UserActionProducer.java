package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;
import ru.practicum.config.KafkaConfig;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Component
@Slf4j
public class UserActionProducer implements AutoCloseable {

    private final KafkaProducer<Long, SpecificRecordBase> producer;
    private final KafkaConfig config;

    public UserActionProducer(KafkaConfig kafkaConfig) {
        this.config = kafkaConfig;
        this.producer = new KafkaProducer<>(this.config.getProperties());
    }

    public void send(Instant timestamp, KafkaConfig.KafkaTopic topic, SpecificRecordBase event, Long key) {
        if (event == null) {
            log.warn("Send null event to topic '{}'", topic.name());
            return;
        }
        log.info("<= Send: {} to topic: {}", event, topic.name());

        ProducerRecord<Long, SpecificRecordBase> record = new ProducerRecord<>(config.getTopics().get(topic),
                null,
                timestamp.toEpochMilli(),
                key,
                event);

        Future<RecordMetadata> sendResult = producer.send(record);
        producer.flush();


        try {
            sendResult.get();
            log.info("=> Successfully sent event {} to topic '{}' (key: {})", event.getClass().getSimpleName(),
                    topic.name(), key);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to send event {} to topic '{}' (key: {}). Error: {}",
                    event.getClass().getSimpleName(), topic.name(), key, e.getMessage(), e);
        }
    }

    @Override
    public void close() throws Exception {
        producer.flush();
        producer.close(Duration.ofSeconds(10));
    }
}
