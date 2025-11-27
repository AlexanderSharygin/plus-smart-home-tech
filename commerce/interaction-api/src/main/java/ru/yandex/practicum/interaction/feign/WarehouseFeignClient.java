package ru.yandex.practicum.interaction.feign;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.dto.*;
import ru.yandex.practicum.interaction.resilience.WarehouseFeignClientFallback;

import java.util.Map;
import java.util.UUID;


@FeignClient(name = "warehouse", path = "/api/v1/warehouse", fallback = WarehouseFeignClientFallback.class)
public interface WarehouseFeignClient {

    @GetMapping("/address")
    AddressDto getWarehouseAddress();

    @PutMapping
    void addNewProduct(@RequestBody NewInWarehouseRequest request);

    @PostMapping("/add")
    void addToWarehouse(@RequestBody AddToWarehouseRequest request);

    @PostMapping("/check")
    BookedProductsDto checkProductAvailability(@RequestBody ShoppingCartDto cart);

    @PostMapping("/return")
    void acceptReturn(@RequestBody Map<UUID, Integer> products);

    @PostMapping("/assembly")
    BookedProductsDto assemblyForOrder(@RequestBody @Valid AssemblyProductsForOrderRequest request);

    @PostMapping("/shipped")
    void shippedToDelivery(ShippedToDeliveryRequest request);
}