package ru.yandex.practicum.interaction.dto;

import jakarta.validation.constraints.Min;

public record DimensionDto(@Min(1) Double width, @Min(1) Double height, @Min(1) Double depth) {
}