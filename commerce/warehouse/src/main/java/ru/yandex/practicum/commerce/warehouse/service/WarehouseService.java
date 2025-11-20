package ru.yandex.practicum.commerce.warehouse.service;


import ru.yandex.practicum.interaction.dto.*;


public interface WarehouseService {

    void addNewProductToWarehouse(NewInWarehouseRequest request);

    BookedProductsDto checkProductAvailability(ShoppingCartDto cart);

    void takeProductToWarehouse(AddToWarehouseRequest request);

    AddressDto getWarehouseAddress();
}