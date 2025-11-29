package ru.yandex.practicum.interaction.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import ru.yandex.practicum.interaction.enums.OrderState;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record OrderDto(
        @NotNull UUID orderId,
        UUID shoppingCartId,
        @NotNull Map<UUID, Long> products,
        UUID paymentId,
        UUID deliveryId,
        OrderState state,
        Double deliveryWeight,
        Double deliveryVolume,
        Boolean fragile,
        @Positive
        BigDecimal totalPrice,
        @PositiveOrZero
        BigDecimal deliveryPrice,
        @Positive
        BigDecimal productPrice
) {
}