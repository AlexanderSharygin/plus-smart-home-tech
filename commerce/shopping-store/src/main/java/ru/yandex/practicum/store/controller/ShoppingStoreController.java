package ru.yandex.practicum.store.controller;

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
import ru.yandex.practicum.store.service.ShoppingStoreService;

import java.util.UUID;

/**
 * REST controller for managing products in the shopping store.
 */
@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ShoppingStoreController implements ShoppingStoreFeignClient {

    private final ShoppingStoreService service;

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        log.info("ShoppingStoreController: -> Создание нового товара в ассортименте: {}", productDto);
        return service.createNewProduct(productDto);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        log.info("ShoppingStoreController: -> Обновление товара в ассортименте: {}", productDto);
        return service.updateProduct(productDto);
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        log.info("ShoppingStoreController: -> Получение сведения по товару из БД по id: {}", productId);
        return service.getProductById(productId);
    }

    @Override
    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        log.info("ShoppingStoreController: -> Получение списка товаров по типу: {}", category);
        return service.getProducts(category, pageable);
    }

    @Override
    public boolean removeProductFromStore(UUID productId) {
        log.info("ShoppingStoreController: -> Удаление товара с id: {}", productId);
        return service.deleteProduct(productId);
    }

    @Override
    public boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        log.info("ShoppingStoreController: -> Установка статуса по товару: {}", request);
        return service.setQuantityState(request);
    }
}
