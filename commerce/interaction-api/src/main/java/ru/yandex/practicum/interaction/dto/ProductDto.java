package ru.yandex.practicum.interaction.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.interaction.enums.ProductCategory;
import ru.yandex.practicum.interaction.enums.ProductState;
import ru.yandex.practicum.interaction.enums.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {

    UUID productId;

    @NotBlank
    String productName;

    @NotBlank
    String description;

    String imageSrc;

    @NotNull
    QuantityState quantityState;

    @NotNull
    ProductState productState;

    ProductCategory productCategory;

    @NotNull
    @Digits(integer = 19, fraction = 2)
    @DecimalMin(value = "1.0")
    private BigDecimal price;

}
