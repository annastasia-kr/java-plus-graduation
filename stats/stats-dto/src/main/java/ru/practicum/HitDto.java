package ru.practicum;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @NotBlank(message = "Uri must not be blank")
    @JsonFormat(pattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")
    private String ip;

    private LocalDateTime timestamp;
}