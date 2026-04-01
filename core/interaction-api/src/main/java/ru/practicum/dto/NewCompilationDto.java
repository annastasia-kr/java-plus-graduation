package ru.practicum.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {
    private Boolean pinned;

    @NotBlank(message = "'title' is required.")
    @Size(min = 1, max = 50)
    private String title;

    private List<Long> events;
}