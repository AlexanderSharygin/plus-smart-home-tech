package ru.yandex.practicum.interaction.exception.model;

import java.util.UUID;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
