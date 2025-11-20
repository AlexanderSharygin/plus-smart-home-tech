package ru.yandex.practicum.interaction.dto;

import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.interaction.enums.QuantityState;

import java.util.UUID;


public record SetProductQuantityStateRequest(@NotNull UUID productId, @NotNull QuantityState quantityState) {
}