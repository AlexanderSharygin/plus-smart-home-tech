package ru.yandex.practicum.commerce.warehouse.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.warehouse.service.WarehouseService;
import ru.yandex.practicum.interaction.dto.*;
import ru.yandex.practicum.interaction.feign.WarehouseFeignClient;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController implements WarehouseFeignClient {

    private final WarehouseService service;

    @Override
    public void addNewProduct(NewInWarehouseRequest request) {
        log.info("Добавление товара на склад: {}", request);
        service.addNewProductToWarehouse(request);
    }

    @Override
    public BookedProductsDto checkProductAvailability(ShoppingCartDto cart) {
        log.info("Проверка количества товаров на складе: {}", cart);
        return service.checkProductAvailability(cart);
    }

    @Override
    public void takeToWarehouse(AddToWarehouseRequest request) {
        log.info("Прием товара на склад: {}", request);
        service.takeProductToWarehouse(request);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        log.info("Получение адреса склада");
        return service.getWarehouseAddress();
    }
}