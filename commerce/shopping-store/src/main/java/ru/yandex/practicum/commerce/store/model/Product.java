package ru.yandex.practicum.commerce.store.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import ru.yandex.practicum.interaction.enums.ProductCategory;
import ru.yandex.practicum.interaction.enums.ProductState;
import ru.yandex.practicum.interaction.enums.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Table(name = "products", schema = "store")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "product_id", updatable = false, nullable = false)
  private UUID productId;

  @Column(name = "product_name", nullable = false)
  private String productName;

  @Column(nullable = false)
  private String description;

  @Column(name = "image_src", length = 512)
  private String imageSrc;

  @Column(name = "quantity_state", nullable = false)
  @Enumerated(EnumType.STRING)
  private QuantityState quantityState;

  @Column(name = "product_state", nullable = false)
  @Enumerated(EnumType.STRING)
  private ProductState productState;

  @Column(name = "product_category")
  @Enumerated(EnumType.STRING)
  private ProductCategory productCategory;

  @Min(1)
  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal price;
}