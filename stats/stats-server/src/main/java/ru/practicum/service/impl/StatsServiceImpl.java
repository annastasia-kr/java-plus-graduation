package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.HitDto;
import ru.practicum.StatsDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.model.Hit;
import ru.practicum.repository.StatsRepository;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static ru.practicum.mapper.HitMapper.toHit;
import static ru.practicum.mapper.HitMapper.toHitDto;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Transactional
    @Override
    public HitDto create(HitDto hitDto) {
        log.warn("Create hit {}", hitDto);
        Hit createdHit = toHit(hitDto);
        Hit hit = statsRepository.save(createdHit);
        log.warn("The hit {} has been created.", createdHit);
        return toHitDto(hit);
    }

    @Override
    public Collection<StatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start.isAfter(end))
            throw new ValidationException("The start date must be earlier than the end date.");

        if (unique) {
            if (uris != null && !uris.isEmpty())
                return statsRepository.findUniqueStatsByUrisAndTimestampBetween(start, end, uris);
            return statsRepository.findUniqueStatsByTimestampBetween(start, end);
        }
            if (uris != null && !uris.isEmpty())
                return statsRepository.findStatsByUrisAndTimestampBetween(start, end, uris);

            return statsRepository.findStatsByTimestampBetween(start, end);
    }

}