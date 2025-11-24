package ru.yandex.practicum.commerce.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.payment.model.Payment;
import ru.yandex.practicum.commerce.payment.model.PaymentMapper;
import ru.yandex.practicum.commerce.payment.repository.PaymentRepository;
import ru.yandex.practicum.interaction.dto.OrderDto;
import ru.yandex.practicum.interaction.dto.PaymentDto;
import ru.yandex.practicum.interaction.dto.ProductDto;
import ru.yandex.practicum.interaction.enums.PaymentState;
import ru.yandex.practicum.interaction.exception.model.ConflictException;
import ru.yandex.practicum.interaction.exception.model.NotFoundException;
import ru.yandex.practicum.interaction.feign.OrderFeignClient;
import ru.yandex.practicum.interaction.feign.ShoppingStoreFeignClient;


import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;
    private final PaymentMapper mapper;
    private final OrderFeignClient orderClient;
    private final ShoppingStoreFeignClient storeClient;

    @Value("${payment.vat}")
    private BigDecimal vat;

    private void checkProductPrice(OrderDto order)    {
        if (order.getTotalPrice() == null || order.getDeliveryPrice() == null) {
            log.error("В заказе отсутствует необходимая информация!");
            throw new ConflictException("В заказе отсутствует необходимая информация!");
        }
    }

    @Override
    @Transactional
    public PaymentDto payment(OrderDto order) { log.info("PaymentService: -> Формирование оплаты для заказа: {}", order);
        checkProductPrice(order);

        Payment payment = Payment.builder()
                .productsTotal(order.getProductPrice())
                .deliveryTotal(order.getDeliveryPrice())
                .totalPayment(order.getTotalPrice())
                .feeTotal(order.getTotalPrice().multiply(vat))
                .paymentState(PaymentState.PENDING)
                .orderId(order.getOrderId())
                .build();

        PaymentDto savedPayment = mapper.toDto(repository.save(payment));

        log.info("PaymentService: -> Сформированная оплата заказа: {}", savedPayment);
        return savedPayment;
    }

    @Override
    public BigDecimal getTotalCost(OrderDto order) {
        checkProductPrice(order);

        BigDecimal productTotalCost = productCost(order);
        BigDecimal deliveryPrice = order.getDeliveryPrice();
        BigDecimal tax = productTotalCost.multiply(vat);

        BigDecimal totalCost = productTotalCost.add(deliveryPrice).add(tax);

        log.info("PaymentService: -> Полная стоимость заказа: {}", totalCost);
        return totalCost;
    }

    @Override
    @Transactional
    public void paymentSuccess(UUID paymentId) {
        log.info("PaymentService: -> Метод для эмуляции успешной оплаты: {}", paymentId);

        Payment payment = repository.findPaymentByPaymentId(paymentId)
                .orElseThrow(() -> new NotFoundException("Заказ не найден"));
        payment.setPaymentState(PaymentState.SUCCESS);
        orderClient.payment(payment.getOrderId());
        repository.save(payment);

        log.info("PaymentService: -> Успешная оплата в платежном шлюзе: {}", paymentId);
    }

    @Override
    public BigDecimal productCost(OrderDto order) {
        log.info("PaymentService: -> Расчёт стоимости товаров в заказе: {}", order);

        Map<UUID, Long> products = order.getProducts();

        if (products == null) {
            throw new ConflictException("Нет продуктов в заказе");
        }

        BigDecimal totalCost = BigDecimal.ZERO;

        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            ProductDto product = storeClient.getProduct(entry.getKey());
            BigDecimal productPrice = product.price();
            BigDecimal total = productPrice.multiply(BigDecimal.valueOf(entry.getValue()));
            totalCost = totalCost.add(total);
        }

        log.info("PaymentService: -> Расчёт стоимости товаров в заказе: {}", totalCost);
        return totalCost;
    }

    @Override
    @Transactional
    public void paymentFailed(UUID paymentId) {
        log.info("PaymentService: -> Метод для эмуляции отказа в оплате платежного шлюза: {}", paymentId);

        Payment payment = repository.findPaymentByPaymentId(paymentId)
                .orElseThrow(() -> new NotFoundException("Заказ не найден"));
        payment.setPaymentState(PaymentState.FAILED);
        orderClient.paymentFailed(payment.getOrderId());
        repository.save(payment);

        log.info("PaymentService: -> Отказ при оплате заказа: {}", paymentId);

    }
}