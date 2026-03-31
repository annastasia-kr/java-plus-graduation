package ru.practicum.controller.compilation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.CompilationDto;
import ru.practicum.event.dto.NewCompilationDto;
import ru.practicum.event.dto.UpdateCompilationRequest;
import ru.practicum.service.compilation.CompilationService;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Validated
public class AdminCompilationController {
    private final CompilationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@RequestBody @Valid NewCompilationDto newCompilationData) {
        return service.create(newCompilationData);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable @Positive @NotNull Long compId) {
        service.deleteById(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateById(@PathVariable @Positive @NotNull Long compId,
                                     @RequestBody @Valid UpdateCompilationRequest compilationData) {
        return service.updateById(compId, compilationData);
    }
}
