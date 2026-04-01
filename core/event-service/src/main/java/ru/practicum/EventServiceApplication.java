package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.practicum.client.impl.StatsClient;
import ru.practicum.request.client.RequestClient;
import ru.practicum.user.client.UserClient;

@SpringBootApplication
@EnableFeignClients(clients = {UserClient.class, RequestClient.class})
@EnableDiscoveryClient
public class EventServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventServiceApplication.class, args);
    }
}
