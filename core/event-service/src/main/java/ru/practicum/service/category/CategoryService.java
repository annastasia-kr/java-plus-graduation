package ru.practicum.service.category;

import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> findAll(Integer from, Integer size);

    CategoryDto findById(Long catId);

    CategoryDto create(NewCategoryDto o);

    void deleteById(Long catId);

    CategoryDto updateById(Long catId, NewCategoryDto o);
}
