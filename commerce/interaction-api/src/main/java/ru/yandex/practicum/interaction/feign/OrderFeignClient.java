package ru.yandex.practicum.interaction.feign;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.dto.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.dto.OrderDto;
import ru.yandex.practicum.interaction.dto.Pageable;
import ru.yandex.practicum.interaction.dto.ProductReturnRequest;

import java.util.UUID;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderFeignClient {

    @GetMapping
    Page<OrderDto> getOrders(@RequestParam("username") String username,
                             @SpringQueryMap Pageable pageable);

    @PutMapping
    OrderDto create(@RequestBody @Valid CreateNewOrderRequest request);

    @PostMapping("/return")
    OrderDto productReturn(@RequestBody @Valid ProductReturnRequest request);

    @PostMapping("/payment")
    OrderDto payment(@RequestBody UUID orderId);

    @PostMapping("/payment/failed")
    OrderDto paymentFailed(@RequestBody UUID orderId);

    @PostMapping("/delivery")
    OrderDto delivery(@RequestBody UUID orderId);

    @PostMapping("/delivery/failed")
    OrderDto deliveryFailed(@RequestBody UUID orderId);

    @PostMapping("/completed")
    OrderDto complete(@RequestBody UUID orderId);

    @PostMapping("/calculate/total")
    OrderDto calculateTotal(@RequestBody UUID orderId);

    @PostMapping("/calculate/delivery")
    OrderDto calculateDelivery(@RequestBody UUID orderId);

    @PostMapping("/assembly")
    OrderDto assembly(@RequestBody UUID orderId);

    @PostMapping("/assembly/failed")
    OrderDto assemblyFailed(@RequestBody UUID orderId);
}