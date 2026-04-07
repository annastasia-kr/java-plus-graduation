package ru.practicum.controller.category;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.service.category.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated
public class PublicCategoryController {

    private final CategoryService service;

    @GetMapping
    public List<CategoryDto> findAll(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                     @RequestParam(defaultValue = "10") @Positive Integer size) {
        return service.findAll(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto findById(@PathVariable @NotNull Long catId) {
        return service.findById(catId);
    }

}