package ru.practicum.client.impl;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "stats-server")
public interface StatsClient {
    void saveHit(jakarta.servlet.http.HttpServletRequest request);

    List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

}