package ru.yandex.practicum.commerce.warehouse.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Dimension {

    @Positive
    Double width;

    @Positive
    Double height;

    @Positive
    Double depth;
}