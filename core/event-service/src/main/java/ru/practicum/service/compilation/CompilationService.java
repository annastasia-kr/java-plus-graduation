package ru.practicum.service.compilation;

import ru.practicum.event.dto.CompilationDto;
import ru.practicum.event.dto.NewCompilationDto;
import ru.practicum.event.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> findAll(Boolean pinned, Integer from, Integer size);

    CompilationDto findById(Long compId);

    CompilationDto create(NewCompilationDto o);

    void deleteById(Long compId);

    CompilationDto updateById(Long compId, UpdateCompilationRequest o);
}