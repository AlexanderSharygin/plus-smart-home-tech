package ru.yandex.practicum.commerce.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.payment.service.PaymentService;
import ru.yandex.practicum.interaction.dto.OrderDto;
import ru.yandex.practicum.interaction.dto.PaymentDto;
import ru.yandex.practicum.interaction.feign.PaymentFeignClient;


import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController implements PaymentFeignClient {

    private final PaymentService service;

    @Override
    public PaymentDto payment(OrderDto order) {
        log.info("PaymentController: -> Формирование оплаты для заказа: {}", order);
        PaymentDto dto = service.payment(order);
        return dto;
    }

    @Override
    public BigDecimal getTotalCost(OrderDto order) {
        log.info("PaymentController: -> Расчёт полной стоимости заказа: {}", order);
        return service.getTotalCost(order);
    }

    @Override
    public void paymentSuccess(UUID paymentId) {
        log.info("PaymentController: -> Метод для эмуляции успешной оплаты: {}", paymentId);
        service.paymentSuccess(paymentId);
    }

    @Override
    public BigDecimal productCost(OrderDto order) {
        log.info("PaymentController: -> Расчёт стоимости товаров в заказе: {}", order);
        return service.productCost(order);
    }

    @Override
    public void paymentFailed(UUID paymentId) {
        log.info("PaymentController: -> Метод для эмуляции отказа в оплате платежного шлюза: {}", paymentId);
        service.paymentFailed(paymentId);
    }
}