package ru.yandex.practicum.interaction.resilience;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.dto.*;
import ru.yandex.practicum.interaction.exception.model.ServiceUnavailableException;
import ru.yandex.practicum.interaction.feign.WarehouseFeignClient;


@Component
public class WarehouseFeignClientFallback implements WarehouseFeignClient {

    @Override
    public void addNewProduct(NewInWarehouseRequest request) {
        throw new ServiceUnavailableException("Warehouse временно недоступен");
    }

    @Override
    public BookedProductsDto checkProductAvailability(ShoppingCartDto cart) {
        throw new ServiceUnavailableException("Warehouse временно недоступен");
    }

    @Override
    public void takeToWarehouse(AddToWarehouseRequest request) {
        throw new ServiceUnavailableException("Warehouse временно недоступен");
    }

    @Override
    public AddressDto getWarehouseAddress() {
        throw new ServiceUnavailableException("Warehouse временно недоступен");
    }
}