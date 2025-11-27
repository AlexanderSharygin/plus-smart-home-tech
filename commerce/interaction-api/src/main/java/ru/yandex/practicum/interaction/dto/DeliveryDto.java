package ru.yandex.practicum.interaction.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.interaction.enums.DeliveryState;

import java.util.UUID;

public record DeliveryDto(
        @NotNull UUID deliveryId,
        @NotNull AddressDto fromAddress,
        @NotNull AddressDto toAddress,
        @NotNull UUID orderId,
        @NotNull DeliveryState deliveryState
) {}