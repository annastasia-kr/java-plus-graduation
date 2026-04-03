package ru.practicum.client;

import ru.practicum.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsClient {
    void saveHit(jakarta.servlet.http.HttpServletRequest request);

    List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

}