package ru.practicum.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.StatsDto;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatsRepository extends JpaRepository<Hit, Long> {
    @Query(value = """
        SELECT new ru.practicum.StatsDto(app, uri, COUNT(DISTINCT ip) AS hits)
        FROM Hit
        WHERE timestamp BETWEEN :start AND :end
        AND uri IN :uris
        GROUP BY app, uri
        ORDER BY hits DESC
    """)
    List<StatsDto> findUniqueStatsByUrisAndTimestampBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );

    @Query(value = """
        SELECT new ru.practicum.StatsDto(app, uri, COUNT(ip) AS hits)
        FROM Hit
        WHERE timestamp BETWEEN :start AND :end
        AND uri IN :uris
        GROUP BY app, uri
        ORDER BY hits DESC
    """)
    List<StatsDto> findStatsByUrisAndTimestampBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );

    @Query("""
            SELECT new ru.practicum.StatsDto(app, uri, COUNT(DISTINCT ip) AS hits)
            FROM Hit
            WHERE timestamp BETWEEN :start AND :end
            GROUP BY app, uri
            ORDER BY COUNT(DISTINCT ip) DESC
            """)
    Collection<StatsDto> findUniqueStatsByTimestampBetween(@Param("start") LocalDateTime start, @Param("end")LocalDateTime end);

    @Query("""
            SELECT new ru.practicum.StatsDto(app, uri, COUNT(ip) AS hits)
            FROM Hit
            WHERE timestamp BETWEEN :start AND :end
            GROUP BY app, uri
            ORDER BY COUNT(ip) DESC
            """)
    Collection<StatsDto> findStatsByTimestampBetween(@Param("start")LocalDateTime start, @Param("end")LocalDateTime end);
}