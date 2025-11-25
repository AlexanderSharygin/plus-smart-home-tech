package ru.yandex.practicum.commerce.warehouse.service;


import ru.yandex.practicum.interaction.dto.*;

import java.util.Map;
import java.util.UUID;


public interface WarehouseService {

    void addNewProductToWarehouse(NewInWarehouseRequest request);

    BookedProductsDto checkProductAvailability(ShoppingCartDto cart);

    void takeProductToWarehouse(AddToWarehouseRequest request);

    AddressDto getWarehouseAddress();

    BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request);

    void acceptReturn(Map<UUID, Integer> products);
}