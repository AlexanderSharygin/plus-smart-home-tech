package ru.yandex.practicum.commerce.payment.model;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.dto.PaymentDto;

import java.util.Objects;

@Component
@Slf4j
public class PaymentMapper {


    public PaymentDto toDto(final Payment payment) {
        Objects.requireNonNull(payment);
        return PaymentDto.builder()
                .paymentId(payment.getPaymentId())
                .totalPayment(payment.getTotalPayment())
                .deliveryTotal(payment.getDeliveryTotal())
                .feeTotal(payment.getFeeTotal())
                .build();
    }
}
