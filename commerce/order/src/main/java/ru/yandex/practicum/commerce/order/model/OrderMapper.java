package ru.yandex.practicum.commerce.order.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.dto.OrderDto;


@Component
@Slf4j
public class OrderMapper {

    public OrderDto toDto(final Order order) {
        return new OrderDto(order.getOrderId(), order.getCartId(), order.getProducts(), order.getPaymentId(),
                order.getDeliveryId(), order.getState(), order.getDeliveryWeight(), order.getDeliveryVolume(),
                order.getFragile(), order.getTotalPrice(), order.getDeliveryPrice(), order.getProductPrice());
    }
}
