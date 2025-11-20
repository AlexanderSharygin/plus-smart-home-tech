package ru.yandex.practicum.interaction.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

public record Pageable(Integer page, Integer size,  List<String> sort) {
    public Pageable {
        if (page==null || page < 0) {
            page = 0;
        }
        if (size==null || size <= 0) {
            size = 10;
        }
        if (sort == null || sort.isEmpty()) {
            sort = List.of("productName");
        }
    }
}
