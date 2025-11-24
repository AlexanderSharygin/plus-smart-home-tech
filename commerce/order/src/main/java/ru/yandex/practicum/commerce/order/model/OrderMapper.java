package ru.yandex.practicum.commerce.order.model;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.dto.OrderDto;


import java.util.List;
import java.util.Objects;


@Component
@Slf4j
public class OrderMapper {

  public OrderDto toDto(final Order order) {
    Objects.requireNonNull(order);
    return OrderDto.builder()
        .orderId(order.getOrderId())
        .shoppingCartId(order.getCartId())
        .products(order.getProducts())
        .paymentId(order.getPaymentId())
        .deliveryId(order.getDeliveryId())
        .state(order.getState())
        .deliveryWeight(order.getDeliveryWeight())
        .deliveryVolume(order.getDeliveryVolume())
        .fragile(order.getFragile())
        .totalPrice(order.getTotalPrice())
        .deliveryPrice(order.getDeliveryPrice())
        .productPrice(order.getProductPrice())
        .build();
  }

  public  List<OrderDto> toDto(final List<Order> orders) {
    Objects.requireNonNull(orders);
    return orders.stream()
        .map(this::toDto)
        .toList();
  }
}
