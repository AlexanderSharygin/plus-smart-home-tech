package ru.yandex.practicum.interaction.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.dto.ShoppingCartDto;


import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface ShoppingCartFeignClient {

    @GetMapping
    ShoppingCartDto getCart(@RequestParam("username") String username);

    @PutMapping
    ShoppingCartDto addProduct(@RequestParam("username") String username, @RequestBody Map<UUID, Long> request);

    @DeleteMapping
    void deactivateUserCart(@RequestParam("username") String username);

    @PostMapping("/remove")
    ShoppingCartDto removeProduct(@RequestParam("username") String username, @RequestBody List<UUID> productIds);

    @PostMapping("/change-quantity")
    ShoppingCartDto changeQuantity(@RequestParam("username") String username,
                                   @RequestBody ChangeProductQuantityRequest request);
}