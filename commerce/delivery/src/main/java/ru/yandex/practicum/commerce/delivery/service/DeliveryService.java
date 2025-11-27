package ru.yandex.practicum.commerce.delivery.service;


import ru.yandex.practicum.interaction.dto.DeliveryDto;
import ru.yandex.practicum.interaction.dto.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {

    DeliveryDto createDelivery(DeliveryDto delivery);

    void deliverySuccessful(UUID deliveryId);

    void deliveryPicked(UUID deliveryId);

    void deliveryFailed(UUID deliveryId);

    BigDecimal getDeliveryCost(OrderDto order);

}