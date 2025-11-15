package ru.yandex.practicum.interaction.exception.model;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {

    private static final String MSG_TEMPLATE = "Не найден товар с id = %s ";

    public ProductNotFoundException(UUID id) {
        super(MSG_TEMPLATE.formatted(id));
    }
}
