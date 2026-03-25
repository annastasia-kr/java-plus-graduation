package ru.practicum;

import org.springframework.cloud.openfeign.FeignClient;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "stats-server")
public interface StatsClient {
    void saveHit(jakarta.servlet.http.HttpServletRequest request);

    List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

}