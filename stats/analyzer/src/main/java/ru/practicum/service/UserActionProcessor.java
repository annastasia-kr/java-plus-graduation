package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.config.KafkaConfig;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.service.handler.UserActionHandler;

import java.time.Duration;
import java.util.List;

@Component
@Slf4j
public class UserActionProcessor implements Runnable {

    private static final Duration TIMEOUT = Duration.ofMillis(150);

    private final KafkaConsumer<Long, UserActionAvro> consumer;
    private final UserActionHandler handler;
    private final String topic;

    public UserActionProcessor(KafkaConfig config, UserActionHandler handler) {
        this.handler = handler;
        this.consumer = new KafkaConsumer<>(config.getActionConsumerProperties());
        this.topic = config.getTopics().get(KafkaConfig.KafkaTopic.USER_ACTIONS);
    }


    @Override
    public void run() {
        try {
            // ... подписка на топик ...
            consumer.subscribe(List.of(this.topic));

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                consumer.wakeup();
            }));
            // Цикл обработки событий
            while (true) {
                ConsumerRecords<Long, UserActionAvro> records = consumer.poll(TIMEOUT);

                if (records.isEmpty()) {
                    log.debug("No new records received after {} ms", TIMEOUT);
                    Thread.sleep(100);
                    continue;
                }

                // ... реализация цикла опроса ...
                for (ConsumerRecord<Long, UserActionAvro> record : records) {
                    UserActionAvro hubEventAvro = record.value();
                    handler.handle(hubEventAvro);
                }
            }
        } catch (WakeupException ignored) {
            log.info("User action consumer wakeup triggered, initiating shutdown...");
        } catch (Exception e) {
            log.error("Unexpected error in user action consumer loop: {}", e.getMessage(), e);
        } finally {
            try {
                consumer.commitAsync((offsets, exception) -> {
                    if (exception != null) {
                        log.warn("Offset {} commit error: {}", offsets, exception);
                    }
                });
            } finally {
                log.info("Closing user action consumer");
                consumer.close();
            }
        }
    }

    public void stop() {
        consumer.wakeup();
    }
}
