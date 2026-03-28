package ru.practicum.mapper;

import java.time.LocalDateTime;

import lombok.experimental.UtilityClass;
import ru.practicum.HitDto;
import ru.practicum.model.Hit;

@UtilityClass
public class HitMapper {

    public static Hit toHit(HitDto hitDto) {
        Hit hit = new Hit();
        hit.setId(hitDto.getId());
        hit.setApp(hitDto.getApp());
        hit.setIp(hitDto.getIp());
        hit.setUri(hitDto.getUri());
        hit.setTimestamp(hitDto.getTimestamp() != null ? hitDto.getTimestamp() : LocalDateTime.now());
        return hit;
    }

    public static HitDto toHitDto(Hit hit) {
        HitDto hitDto = new HitDto();
        hitDto.setId(hit.getId());
        hitDto.setApp(hit.getApp());
        hitDto.setIp(hit.getIp());
        hitDto.setUri(hit.getUri());
        hitDto.setTimestamp(hit.getTimestamp());
        return hitDto;
    }
}
