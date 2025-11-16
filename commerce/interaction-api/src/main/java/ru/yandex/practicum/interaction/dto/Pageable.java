package ru.yandex.practicum.interaction.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

public record Pageable(@PositiveOrZero int page, @Positive int size, @Positive List<String> sort) {
}
