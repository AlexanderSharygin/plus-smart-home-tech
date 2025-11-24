package ru.yandex.practicum.commerce.payment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.proxy.HibernateProxy;
import ru.yandex.practicum.interaction.enums.PaymentState;


import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id")
    UUID paymentId;

    @NotNull
    @Column(name = "order_id")
    UUID orderId;

    @Column(name = "products_total", nullable = false)
    BigDecimal productsTotal;

    @Column(name = "delivery_total", nullable = false)
    BigDecimal deliveryTotal;

    @Column(name = "total_payment", nullable = false)
    BigDecimal totalPayment;

    @Column(name = "fee_total", nullable = false)
    BigDecimal feeTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_state", nullable = false)
    PaymentState paymentState;
}