package ru.practicum;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "stats-server")
public interface StatsClient {

    @PostMapping("/hit")
    void saveHit(jakarta.servlet.http.HttpServletRequest request);

    @GetMapping("/stats")
    List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

}