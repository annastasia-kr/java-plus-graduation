package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.event.dto.CategoryDto;
import ru.practicum.event.dto.NewCategoryDto;
import ru.practicum.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toCategoryDto(Category category);

    @Mapping(target = "id", ignore = true)
    Category toCategory(NewCategoryDto category);
}
