package ru.yandex.practicum.commerce.delivery.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.delivery.service.DeliveryService;
import ru.yandex.practicum.interaction.dto.DeliveryDto;
import ru.yandex.practicum.interaction.dto.OrderDto;
import ru.yandex.practicum.interaction.feign.DeliveryFeignClient;


import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery")
public class DeliveryController implements DeliveryFeignClient {

    private final DeliveryService service;

    @Override
    public DeliveryDto planDelivery(DeliveryDto delivery) {
        log.info("DeliveryController -> Создание новой доставки: {}", delivery);
        return service.planDelivery(delivery);
    }

    @Override
    public void deliverySuccessful(UUID deliveryId) {
        log.info("DeliveryController -> Эмуляция успешной доставки с id: {}", deliveryId);
        service.deliverySuccessful(deliveryId);
    }

    @Override
    public void deliveryPicked(UUID deliveryId) {
        log.info("DeliveryController -> Эмуляция получения товара в доставку с id: {}", deliveryId);
        service.deliveryPicked(deliveryId);
    }

    @Override
    public void deliveryFailed(UUID deliveryId) {
        log.info("DeliveryController -> Эмуляция неудачного вручения товара с id: {}", deliveryId);
        service.deliveryFailed(deliveryId);
    }

    @Override
    public BigDecimal deliveryCost(OrderDto order) {
        log.info("DeliveryController -> Расчёт полной стоимости доставки заказа: {}", order);
        return service.deliveryCost(order);
    }
}