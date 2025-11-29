package ru.yandex.practicum.commerce.warehouse.service;


import ru.yandex.practicum.interaction.dto.*;

import java.util.Map;
import java.util.UUID;


public interface WarehouseService {

    AddressDto getWarehouseAddress();

    void addNewProduct(NewInWarehouseRequest request);

    void addToWarehouse(AddToWarehouseRequest request);

    BookedProductsDto checkProductAvailability(ShoppingCartDto cart);


    BookedProductsDto assemblyForOrder(AssemblyProductsForOrderRequest request);

    void acceptReturn(Map<UUID, Integer> products);
}