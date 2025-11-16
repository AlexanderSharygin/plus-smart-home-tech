package ru.yandex.practicum.commerce.store.service;

import org.springframework.data.domain.Page;

import ru.yandex.practicum.interaction.dto.Pageable;
import ru.yandex.practicum.interaction.enums.ProductCategory;
import ru.yandex.practicum.interaction.dto.ProductDto;
import ru.yandex.practicum.interaction.dto.SetProductQuantityStateRequest;

import java.util.UUID;


public interface ShoppingStoreService {

  ProductDto createProduct(ProductDto productDto);

  ProductDto updateProduct(ProductDto productDto);

  ProductDto getProductById(UUID productId);

  Page<ProductDto> getProductsByCategory(ProductCategory productCategory, Pageable pageable);

  boolean deleteProduct(UUID productId);

  boolean setQuantityState(SetProductQuantityStateRequest request);

}
