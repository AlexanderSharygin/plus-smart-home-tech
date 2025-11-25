package ru.yandex.practicum.commerce.order.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.interaction.enums.OrderState;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders", schema = "orders")
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id")
    UUID orderId;

    @Column(name = "user_name")
    String userName;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    OrderState state;

    @ElementCollection
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity", nullable = false)
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    Map<UUID, Long> products;

    @Column(name = "cart_id", nullable = false)
    UUID cartId;

    @Column(name = "delivery_id")
    UUID deliveryId;

    @Column(name = "payment_id")
    UUID paymentId;

    @Column(name = "delivery_volume")
    Double deliveryVolume;

    @Column(name = "delivery_weight")
    Double deliveryWeight;

    @Column(name = "fragile")
    Boolean fragile;

    @Column(name = "total_price")
    BigDecimal totalPrice;

    @Column(name = "product_price")
    BigDecimal productPrice;

    @Column(name = "delivery_price")
    BigDecimal deliveryPrice;
}
