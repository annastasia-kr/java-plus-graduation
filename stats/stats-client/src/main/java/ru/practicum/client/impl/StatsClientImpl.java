package ru.practicum.client.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.StatsDto;
import ru.practicum.exception.StatsServerUnavailable;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class StatsClientImpl implements StatsClient {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final long backOffPeriod = 10000L;
    private static final int maxAttempts = 5;

    private final RestClient restClient;
    private final String app;
    private final DiscoveryClient discoveryClient;
    private final String serviceId;
    private final RetryTemplate retryTemplate;

    public StatsClientImpl(
            @Value("${services.stats-server-id}") String serviceId,
            @Value("${spring.application.name}") String app,
            DiscoveryClient discoveryClient,
            @Value("${client.stats.connect-timeout:5000}") int connectTimeoutMillis,
            @Value("${client.stats.read-timeout:10000}") int readTimeoutMillis) {
        this.restClient = RestClient.builder().build();
        this.app = app;
        this.discoveryClient = discoveryClient;

        this.retryTemplate = createRetryTemplate();
        this.serviceId = serviceId;
    }

    private RetryTemplate createRetryTemplate() {
        RetryTemplate template = new RetryTemplate();

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(backOffPeriod);
        template.setBackOffPolicy(backOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(maxAttempts);
        template.setRetryPolicy(retryPolicy);

        return template;
    }

    @Override
    public void saveHit(HttpServletRequest request) {

        try {
            Map<String, Object> hitData = new HashMap<>();
            hitData.put("app", app);
            hitData.put("uri", request.getRequestURI());
            hitData.put("ip", request.getRemoteAddr());
            hitData.put("timestamp", LocalDateTime.now().format(FORMATTER));

            ResponseEntity<Void> response = restClient.post()
                    .uri(makeURI() + "/hit")
                    .body(hitData)
                    .retrieve()
                    .toBodilessEntity();

            if (response.getStatusCode().is2xxSuccessful()) {
                log.debug("Hit saved: app={}, uri={}, ip={}", app, request.getRequestURI(),
                        request.getRemoteAddr());
            } else {
                log.error("Failed to save hit. Status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error saving hit: {}", e.getMessage());
        }
    }

    @Override
    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(makeURI() + "/stats")
                    .queryParam("start", start.format(FORMATTER))
                    .queryParam("end", end.format(FORMATTER));

            if (uris != null && !uris.isEmpty()) {
                uriBuilder.queryParam("uris", String.join(",", uris));
            }

            if (unique != null) {
                uriBuilder.queryParam("unique", unique);
            }

            String url = uriBuilder.build().toUriString();

            List response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<StatsDto>>() {
                    });

            return response;
        } catch (Exception e) {
            log.error("Error getting stats: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private URI makeURI() {
        ServiceInstance instance = retryTemplate.execute(context -> getInstance());

        String scheme = instance.getScheme() != null ? instance.getScheme() : "http";
        URI uri = URI.create(scheme + "://" + instance.getHost() + ":" + instance.getPort());

        log.debug("Successfully created URI for statistics service: {}", uri);

        return uri;
    }

    private ServiceInstance getInstance() {
        try {
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
            if (instances == null || instances.isEmpty()) {
                throw new StatsServerUnavailable("Statistics service is unavailable!");
            }
            return instances.getFirst();
        } catch (Exception e) {
            throw new StatsServerUnavailable("Statistics service discovery failed!");
        }
    }
}
