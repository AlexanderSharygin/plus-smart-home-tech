package ru.yandex.practicum.commerce.store.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.dto.Pageable;
import ru.yandex.practicum.interaction.enums.ProductCategory;
import ru.yandex.practicum.interaction.dto.ProductDto;
import ru.yandex.practicum.interaction.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.interaction.feign.ShoppingStoreFeignClient;
import ru.yandex.practicum.commerce.store.service.ShoppingStoreService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ShoppingStoreController implements ShoppingStoreFeignClient {

    private final ShoppingStoreService service;

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        log.info("Создание нового товара: {}", productDto);
        return service.createProduct(productDto);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        log.info("Обновление товара: {}", productDto);
        return service.updateProduct(productDto);
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        log.info("Получение сведения по товару по id: {}", productId);
        return service.getProductById(productId);
    }

    @Override
    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        log.info("Получение списка товаров с типом: {}", category);
        return service.getProductsByCategory(category, pageable);
    }

    @Override
    public boolean removeProduct(UUID productId) {
        log.info("Удаление товара с id: {}", productId);
        return service.deleteProduct(productId);
    }

    @Override
    public boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        log.info("Установка статуса по товару: {}", request);
        return service.setQuantityState(request);
    }
}
