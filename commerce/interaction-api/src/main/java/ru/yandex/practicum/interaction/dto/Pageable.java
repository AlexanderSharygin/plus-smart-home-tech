package ru.yandex.practicum.interaction.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Pageable {

    @PositiveOrZero
    @Builder.Default
    int page = 0;

    @Positive
    @Builder.Default
    int size = 10;

    @Positive
    @Builder.Default
    List<String> sort = List.of("productName");
}
