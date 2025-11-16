package ru.yandex.practicum.commerce.store.model;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.dto.ProductDto;

import java.util.Objects;


@Component
public class ProductMapper {

    public Product toEntity(final ProductDto productDto) {
        Objects.requireNonNull(productDto);
        return Product.builder().productId(productDto.productId()).productName(productDto.productName())
                .description(productDto.description()).imageSrc(productDto.imageSrc())
                .quantityState(productDto.quantityState()).productState(productDto.productState())
                .productCategory(productDto.productCategory()).price(productDto.price()).build();
    }

    public ProductDto toDto(final Product product) {
        return new ProductDto(product.getProductId(), product.getProductName(), product.getDescription(),
                product.getImageSrc(), product.getQuantityState(), product.getProductState(),
                product.getProductCategory(), product.getPrice());
    }
}
