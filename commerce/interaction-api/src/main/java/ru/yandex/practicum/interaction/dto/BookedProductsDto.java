package ru.yandex.practicum.interaction.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookedProductsDto {

    @NotNull
    Double deliveryWeight;

    @NotNull
    Double deliveryVolume;

    @NotNull
    Boolean fragile;
}