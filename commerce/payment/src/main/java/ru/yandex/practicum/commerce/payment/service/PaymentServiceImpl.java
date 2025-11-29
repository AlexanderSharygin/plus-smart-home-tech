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

    @Override
    @Transactional
    public PaymentDto getPayment(OrderDto order) {
        Payment payment = Payment.builder()
                .productsTotal(order.productPrice())
                .deliveryTotal(order.deliveryPrice())
                .totalPayment(order.totalPrice())
                .feeTotal(order.totalPrice().multiply(vat))
                .paymentState(PaymentState.PENDING)
                .orderId(order.orderId())
                .build();
        PaymentDto savedPayment = mapper.toDto(repository.save(payment));
        log.info("Создана оплата заказа с id: {}", order.orderId());

        return savedPayment;
    }

    @Override
    public BigDecimal getTotalCost(OrderDto order) {
        BigDecimal productTotalCost = getProductCost(order);
        BigDecimal deliveryPrice = order.deliveryPrice();
        BigDecimal tax = productTotalCost.multiply(vat);
        BigDecimal totalCost = productTotalCost.add(deliveryPrice).add(tax);
        log.info("Определена полная стоимость заказа: {}", totalCost);

        return totalCost;
    }

    @Override
    @Transactional
    public void paymentSuccess(UUID paymentId) {
        Payment payment = repository.findPaymentByPaymentId(paymentId)
                .orElseThrow(() -> new NotFoundException("Заказ не найден"));
        payment.setPaymentState(PaymentState.SUCCESS);
        orderClient.payment(payment.getOrderId());
        repository.save(payment);
        log.info("Успешная оплата");
    }

    @Override
    public BigDecimal getProductCost(OrderDto order) {
        Map<UUID, Long> products = order.products();
        if (products == null || products.isEmpty()) {
            throw new ConflictException("Нет продуктов в заказе");
        }
        BigDecimal totalCost = BigDecimal.ZERO;
        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            ProductDto product = storeClient.getProduct(entry.getKey());
            BigDecimal productPrice = product.price();
            BigDecimal total = productPrice.multiply(BigDecimal.valueOf(entry.getValue()));
            totalCost = totalCost.add(total);
        }
        log.info("Общая стоимость товаров: {}", totalCost);
        return totalCost;
    }

    @Override
    @Transactional
    public void paymentFailed(UUID paymentId) {
        Payment payment = repository.findPaymentByPaymentId(paymentId)
                .orElseThrow(() -> new NotFoundException("Оплата для заказа не найдена"));
        payment.setPaymentState(PaymentState.FAILED);
        orderClient.paymentFailed(payment.getOrderId());
        repository.save(payment);
        log.info("Оплата завершилась с ошибкой");
    }
}