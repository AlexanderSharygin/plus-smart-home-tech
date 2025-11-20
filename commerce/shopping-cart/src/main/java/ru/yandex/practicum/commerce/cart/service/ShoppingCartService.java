package ru.yandex.practicum.commerce.cart.service;


import ru.yandex.practicum.interaction.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {

    ShoppingCartDto getCart(String username);

    ShoppingCartDto addProduct(String username, Map<UUID, Long> request);

    void deactivatingCart(String username);

    ShoppingCartDto removeProduct(String username, List<UUID> productIds);

    ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request);
}
