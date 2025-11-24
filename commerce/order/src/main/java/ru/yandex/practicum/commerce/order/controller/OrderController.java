package ru.yandex.practicum.commerce.order.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.yandex.practicum.commerce.order.service.OrderService;
import ru.yandex.practicum.interaction.dto.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.dto.OrderDto;
import ru.yandex.practicum.interaction.dto.Pageable;
import ru.yandex.practicum.interaction.dto.ProductReturnRequest;
import ru.yandex.practicum.interaction.feign.OrderFeignClient;


import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController implements OrderFeignClient {

    private final OrderService service;

    public Page<OrderDto> getOrders(String username, Pageable pageable) {
        log.info("OrderController: -> Получение заказов пользователя: {}", username);
        return service.getUserOrders(username, pageable);
    }

    public OrderDto create(CreateNewOrderRequest request) {
        log.info("OrderController -> Создание заказа: {}", request);
        return service.createNewOrder(request);
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest request) {
        log.info("OrderController -> Запрос на возврат заказа: {}", request);
        return service.productReturn(request);
    }

    @Override
    public OrderDto payment(UUID orderId) {
        log.info("OrderController -> Оплата заказа с id: {}", orderId);
        return service.payment(orderId);
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        log.info("OrderController -> Оплата заказа  с id: {} произошла с ошибкой!", orderId);
        return service.paymentFailed(orderId);
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        log.info("OrderController -> Доставка заказа с id: {}!", orderId);
        return service.delivery(orderId);
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        log.info("OrderController -> Доставка заказа  с id: {} произошла с ошибкой!", orderId);
        return service.deliveryFailed(orderId);
    }

    @Override
    public OrderDto complete(UUID orderId) {
        log.info("OrderController -> Завершение заказа с id: {}!", orderId);
        return service.complete(orderId);
    }

    @Override
    public OrderDto calculateTotal(UUID orderId) {
        log.info("OrderController -> Расчёт стоимости заказа с id: {}!", orderId);
        return service.calculateTotal(orderId);
    }

    @Override
    public OrderDto calculateDelivery(UUID orderId) {
        log.info("OrderController -> Расчёт стоимости доставки заказа с id: {}!", orderId);
        return service.calculateDelivery(orderId);
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        log.info("OrderController -> Сборка заказа с id: {}!", orderId);
        return service.assembly(orderId);
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        log.info("OrderController -> Сборка заказа с id - {}, произошла с ошибкой!", orderId);
        return service.assemblyFailed(orderId);
    }
}