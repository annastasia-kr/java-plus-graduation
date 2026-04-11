package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import ru.practicum.service.AggregatorProcessor;


@SpringBootApplication
@ConfigurationPropertiesScan
@EnableDiscoveryClient
public class AggregatorApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AggregatorApplication.class, args);

        AggregatorProcessor aggregator = context.getBean(AggregatorProcessor.class);
        aggregator.start();

    }
}
