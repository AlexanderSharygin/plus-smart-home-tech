package ru.yandex.practicum.interaction.dto;

import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DimensionDto {

    @Min(1)
    Double width;

    @Min(1)
    Double height;

    @Min(1)
    Double depth;
}