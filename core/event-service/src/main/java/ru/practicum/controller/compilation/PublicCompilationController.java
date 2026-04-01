package ru.practicum.controller.compilation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.CompilationDto;
import ru.practicum.service.compilation.CompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Slf4j
public class PublicCompilationController {

    private final CompilationService service;

    @GetMapping
    public List<CompilationDto> findAll(@RequestParam(required = false) Boolean pinned,
                                        @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                        @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.trace("PublicCompilationController findAll");
        return service.findAll(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto findById(@PathVariable @Positive @NotNull Long compId) {
        log.trace("PublicCompilationController findById {}", compId);
        return service.findById(compId);
    }
}