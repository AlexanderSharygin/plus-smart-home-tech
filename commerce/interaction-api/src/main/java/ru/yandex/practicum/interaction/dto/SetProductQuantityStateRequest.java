package ru.yandex.practicum.interaction.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.interaction.enums.QuantityState;

import java.util.UUID;

/**
 * Request to change the quantity state of a product.
 */
@Data
@AllArgsConstructor
public class SetProductQuantityStateRequest {

  @NotNull
  private UUID productId;

  @NotNull
  private QuantityState quantityState;
}
