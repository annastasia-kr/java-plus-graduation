package ru.practicum;

import jakarta.validation.constraints.NotBlank;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HitDto {

    private Long id;

    @NotBlank(message = "App must not be blank")
    private String app;

    @NotBlank(message = "Uri must not be blank")
    private String uri;

    private String ip;

    private LocalDateTime timestamp;
}