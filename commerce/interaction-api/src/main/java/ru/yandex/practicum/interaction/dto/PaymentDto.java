package ru.yandex.practicum.interaction.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentDto {
    UUID paymentId;
    BigDecimal totalPayment;
    BigDecimal deliveryTotal;
    BigDecimal feeTotal;
}