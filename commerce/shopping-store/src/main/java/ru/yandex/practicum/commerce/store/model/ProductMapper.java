package ru.yandex.practicum.commerce.store.model;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.dto.ProductDto;

import java.util.Objects;


@Component
public class ProductMapper {

    public Product toEntity(final ProductDto productDto) {
        Objects.requireNonNull(productDto);
        return Product.builder()
                .productId(productDto.getProductId())
                .productName(productDto.getProductName())
                .description(productDto.getDescription())
                .imageSrc(productDto.getImageSrc())
                .quantityState(productDto.getQuantityState())
                .productState(productDto.getProductState())
                .productCategory(productDto.getProductCategory())
                .price(productDto.getPrice())
                .build();
    }

    public ProductDto toDto(final Product product) {
        Objects.requireNonNull(product);
        return ProductDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .imageSrc(product.getImageSrc())
                .quantityState(product.getQuantityState())
                .productState(product.getProductState())
                .productCategory(product.getProductCategory())
                .price(product.getPrice())
                .build();
    }
}
