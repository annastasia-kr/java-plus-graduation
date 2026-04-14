package ru.practicum.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Properties;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "aggregator.kafka")
public class KafkaConfig {

    EnumMap<KafkaTopic, String> topics = new EnumMap<>(KafkaTopic.class);
    Properties producerProperties;
    Properties consumerProperties;

    public enum KafkaTopic {
        USER_ACTIONS, EVENTS_SIMILARITY}
}