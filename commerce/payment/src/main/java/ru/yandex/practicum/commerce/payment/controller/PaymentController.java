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
        log.info("Начало оплаты для заказа с id: {}", order.orderId());
        return service.getPayment(order);
    }

    @Override
    public BigDecimal getTotalCost(OrderDto order) {
        log.info("Начало расчёта полной стоимости заказа c id: {}", order.orderId());
        return service.getTotalCost(order);
    }

    @Override
    public void paymentSuccess(UUID paymentId) {
        service.paymentSuccess(paymentId);
    }

    @Override
    public BigDecimal productCost(OrderDto order) {
        log.info("Начало расчёта стоимости товаров в заказе с id: {}", order.orderId());
        return service.getProductCost(order);
    }

    @Override
    public void paymentFailed(UUID paymentId) {
        service.paymentFailed(paymentId);
    }
}