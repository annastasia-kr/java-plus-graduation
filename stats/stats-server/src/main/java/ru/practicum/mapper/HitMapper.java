package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.HitDto;
import ru.practicum.StatsDto;
import ru.practicum.model.Hit;

@Mapper(componentModel = "spring")
public interface HitMapper {

    @Mapping(target = "id", ignore = true)
    Hit toHit(HitDto dto);

    @Mapping(target = "hits", ignore = true)
    StatsDto ToStatsDto(Hit hit);

    HitDto toHitDto(Hit hit);
}
