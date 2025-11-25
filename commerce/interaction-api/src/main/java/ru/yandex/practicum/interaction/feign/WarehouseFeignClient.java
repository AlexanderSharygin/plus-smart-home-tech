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

    @PutMapping
    void addNewProduct(@RequestBody NewInWarehouseRequest request);

    @PostMapping("/check")
    BookedProductsDto checkProductAvailability(@RequestBody ShoppingCartDto cart);

    @PostMapping("/add")
    void addToWarehouse(@RequestBody AddToWarehouseRequest request);

    @GetMapping("/address")
    AddressDto getWarehouseAddress();

    @PostMapping("/shipped")
    void shippedToDelivery(ShippedToDeliveryRequest request);

    @PostMapping("/return")
    void acceptReturn(@RequestBody Map<UUID, Integer> products);

    @PostMapping("/assembly")
    BookedProductsDto assemblyProductsForOrder(@RequestBody @Valid AssemblyProductsForOrderRequest request);
}