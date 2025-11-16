package ru.yandex.practicum.commerce.cart.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.cart.service.ShoppingCartService;
import ru.yandex.practicum.interaction.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction.feign.ShoppingCartFeignClient;


import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-cart")
public class ShoppingCartController implements ShoppingCartFeignClient {

    private final ShoppingCartService service;

    @Override
    public ShoppingCartDto getCart(String username) {
        log.info("ShoppingStoreController: -> Получение корзины для пользователя: {}", username);
        return service.getCart(username);
    }

    @Override
    public ShoppingCartDto addProduct(String username, Map<UUID, Long> request) {
        log.info("ShoppingStoreController: -> Добавление товара в корзину: {}", request);
        return service.addProduct(username, request);
    }

    @Override
    public void deactivateUserCart(String username) {
        log.info("ShoppingStoreController: -> Деактивация корзины товаров для пользователя: {}", username);
        service.deactivatingCart(username);
    }

    @Override
    public ShoppingCartDto removeProduct(String username, List<UUID> productIds) {
        log.info("ShoppingStoreController: -> Удаление товара из корзины: {}", productIds);
        return service.removeProduct(username, productIds);
    }

    @Override
    public ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request) {
        log.info("ShoppingStoreController: -> Изменение количества товаров в корзине: {}", request);

        return service.changeQuantity(username, request);
    }
}