package ru.yandex.practicum.commerce.warehouse.entity;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.dto.DimensionDto;
import ru.yandex.practicum.interaction.dto.NewInWarehouseRequest;


@Component
@Slf4j
public class ProductMapper {

    public Product toEntity(final NewInWarehouseRequest request) {
        Boolean fragile = request.getFragile();
        DimensionDto dimensionDto = request.getDimension();
        Dimension dimension = Dimension.builder()
                .width(dimensionDto.getWidth())
                .depth(dimensionDto.getDepth())
                .height(dimensionDto.getHeight())
                .build();

        return Product.builder()
                .productId(request.getProductId())
                .fragile(fragile != null && request.getFragile())
                .dimension(dimension)
                .weight(request.getWeight())
                .quantity(0L)
                .build();
    }

}
