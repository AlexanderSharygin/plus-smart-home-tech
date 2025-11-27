package ru.yandex.practicum.interaction.dto;

import jakarta.validation.constraints.NotNull;


public record CreateNewOrderRequest(@NotNull ShoppingCartDto shoppingCart, @NotNull AddressDto deliveryAddress) {
}
