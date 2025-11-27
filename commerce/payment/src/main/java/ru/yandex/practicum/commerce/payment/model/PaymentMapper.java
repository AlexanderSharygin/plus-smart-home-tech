package ru.yandex.practicum.commerce.payment.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.dto.PaymentDto;

@Component
@Slf4j
public class PaymentMapper {

    public PaymentDto toDto(final Payment payment) {
        return new PaymentDto(payment.getPaymentId(), payment.getTotalPayment(), payment.getDeliveryTotal(),
                payment.getFeeTotal());
    }
}
