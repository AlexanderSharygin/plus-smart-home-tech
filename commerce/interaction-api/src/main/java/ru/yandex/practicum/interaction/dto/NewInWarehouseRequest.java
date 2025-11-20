package ru.yandex.practicum.interaction.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record NewInWarehouseRequest(@NotNull UUID productId, Boolean fragile, @NotNull DimensionDto dimension,
                                    @Min(1) Double weight) {
}