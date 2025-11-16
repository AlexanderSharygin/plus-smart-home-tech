package ru.yandex.practicum.commerce.store.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.store.model.Product;
import ru.yandex.practicum.commerce.store.model.ProductMapper;
import ru.yandex.practicum.commerce.store.repository.ProductRepository;
import ru.yandex.practicum.interaction.dto.Pageable;
import ru.yandex.practicum.interaction.dto.ProductDto;
import ru.yandex.practicum.interaction.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.interaction.enums.ProductCategory;
import ru.yandex.practicum.interaction.enums.ProductState;
import ru.yandex.practicum.interaction.exception.model.NotFoundException;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class ShoppingStoreServiceImpl implements ShoppingStoreService {

    private final ProductRepository repository;

    private final ProductMapper mapper;

    @Override
    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        Product product = repository.save(mapper.toEntity(productDto));
        log.info("Добавлен новый товар: {}", product);
        return mapper.toDto(product);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        Product product = findProductById(productDto.productId());
        updateProductContent(product, productDto);
        Product updatedProduct = repository.save(product);
        log.info("Обновлен товар: {}", updatedProduct);
        return mapper.toDto(updatedProduct);
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        Product product = findProductById(productId);
        log.info("Найдены сведения о товаре: {}", product);
        return mapper.toDto(product);
    }

    @Override
    public Page<ProductDto> getProductsByCategory(ProductCategory productCategory, Pageable pageable) {
        Sort sort = Sort.by(Sort.DEFAULT_DIRECTION, String.join(",", pageable.sort()));
        PageRequest pageRequest = PageRequest.of(pageable.page(), pageable.size(), sort);
        Page<ProductDto> products = repository.findAllByProductCategory(productCategory, pageRequest)
                .map(mapper::toDto);
        log.info("Найден список товаров с типом: {}", productCategory);

        return products;
    }

    @Override
    @Transactional
    public boolean deleteProduct(UUID productId) {
        Product product = findProductById(productId);
        product.setProductState(ProductState.DEACTIVATE);
        repository.save(product);
        log.info("Удален товар: {}", product);
        return true;
    }

    @Override
    @Transactional
    public boolean setQuantityState(SetProductQuantityStateRequest request) {
        Product product = findProductById(request.productId());
        product.setQuantityState(request.quantityState());
        repository.save(product);
        log.info("Обновлен статус товара: {}", request.quantityState());
        return true;
    }

    private Product findProductById(UUID productId) {
        return repository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Продукт с id " + productId + "не найден"));
    }

    private void updateProductContent(final Product target, final ProductDto source) {
        target.setProductName(source.productName() != null ? source.productName() : target.getProductName());
        target.setDescription(source.description() != null ? source.description() : target.getDescription());
        target.setImageSrc(source.imageSrc() != null ? source.imageSrc() : target.getImageSrc());
        target.setQuantityState(source.quantityState() != null ? source.quantityState() : target.getQuantityState());
        target.setProductState(source.productState() != null ? source.productState() : target.getProductState());
        target.setProductCategory(source.productCategory() != null ? source.productCategory()
                : target.getProductCategory());
        target.setPrice(source.price() != null ? source.price() : target.getPrice());
    }
}
