package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitDto;
import ru.practicum.StatsDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<HitDto> create(@RequestBody @Valid HitDto hitDto) {
        log.warn("createHit hit: {}", hitDto);
        HitDto createdHit = statsService.create(hitDto);
        ResponseEntity<HitDto> response = new ResponseEntity<>(createdHit, HttpStatus.CREATED);

        return response;
    }

    @GetMapping("/stats")
    public ResponseEntity<Collection<StatsDto>> get(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(defaultValue = "false") boolean unique) {
        Collection<StatsDto> statsDtoCollection = statsService.get(start, end, uris, unique);
        return ResponseEntity.ok(statsDtoCollection);
    }
}
