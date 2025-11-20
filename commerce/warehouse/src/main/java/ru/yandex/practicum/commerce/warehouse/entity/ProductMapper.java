package ru.yandex.practicum.commerce.warehouse.entity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.dto.DimensionDto;
import ru.yandex.practicum.interaction.dto.NewInWarehouseRequest;


@Component
@Slf4j
public class ProductMapper {

    public Product toEntity(NewInWarehouseRequest request) {
        Boolean fragile = request.fragile();
        DimensionDto dimensionDto = request.dimension();
        Dimension dimension = Dimension.builder().width(dimensionDto.width()).depth(dimensionDto.depth())
                .height(dimensionDto.height()).build();

        return Product.builder().productId(request.productId()).fragile(fragile != null && request.fragile())
                .dimension(dimension).weight(request.weight()).quantity(0L).build();
    }
}