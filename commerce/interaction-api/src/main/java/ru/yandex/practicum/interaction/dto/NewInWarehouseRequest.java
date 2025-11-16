package ru.yandex.practicum.interaction.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewInWarehouseRequest {

    @NotNull
    UUID productId;

    Boolean fragile;

    @NotNull
    DimensionDto dimension;

    @Min(1)
    Double weight;
}