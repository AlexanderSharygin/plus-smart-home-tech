package ru.yandex.practicum.interaction.dto;

import jakarta.validation.constraints.NotNull;

public record BookedProductsDto(@NotNull Double deliveryWeight, @NotNull Double deliveryVolume,
                                @NotNull Boolean fragile) {
}