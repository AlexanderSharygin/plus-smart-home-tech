package ru.yandex.practicum.commerce.cart.model;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.dto.ShoppingCartDto;

@Component
@Slf4j
public class ShoppingCartMapper {

  public ShoppingCartDto toDto(ShoppingCart cart) {
    return ShoppingCartDto.builder()
        .shoppingCartId(cart.getCartId())
        .products(cart.getProducts())
        .build();
  }
}
