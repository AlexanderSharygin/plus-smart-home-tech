package ru.yandex.practicum.commerce.cart.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Entity
@Table(name  = "cart", schema = "cart")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingCart {

  @Id
  @Column(name = "cart_id", nullable = false)
  private UUID cartId;

  @Column(name = "username", nullable = false)
  private String username;

  @Column(name = "active", nullable = false)
  private Boolean active;

  @ElementCollection
  @CollectionTable(name = "cart_products",schema = "cart", joinColumns = @JoinColumn(name = "cart_id"))
  @MapKeyColumn(name = "product_id")
  @Column(name = "quantity")
  private Map<UUID, Long> products = new HashMap<>();
}