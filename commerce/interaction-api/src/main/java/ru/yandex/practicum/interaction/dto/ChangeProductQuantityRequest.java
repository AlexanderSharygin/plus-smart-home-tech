package ru.yandex.practicum.interaction.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record ChangeProductQuantityRequest(@NotNull UUID productId, @PositiveOrZero Long newQuantity) {
}