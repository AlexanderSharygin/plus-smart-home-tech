package ru.yandex.practicum.store.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.dto.Pageable;
import ru.yandex.practicum.interaction.dto.ProductDto;
import ru.yandex.practicum.interaction.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.interaction.enums.ProductCategory;
import ru.yandex.practicum.interaction.enums.ProductState;
import ru.yandex.practicum.interaction.exception.model.ProductNotFoundException;
import ru.yandex.practicum.store.model.Product;
import ru.yandex.practicum.store.model.ProductMapper;
import ru.yandex.practicum.store.repository.ProductRepository;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ShoppingStoreServiceImpl implements ShoppingStoreService {

    private final ProductRepository repository;

    private final ProductMapper mapper;

    @Override
    @Transactional
    public ProductDto createNewProduct(ProductDto productDto) {
        Product product = repository.save(mapper.toEntity(productDto));
        log.info("ShoppingStoreServiceImpl -> Добавлен новый товар: {}", product);
        return mapper.toDto(product);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        Product product = findProductById(productDto.getProductId());
        updateProductContent(product, productDto);
        Product updatedProduct = repository.save(product);
        log.info("ShoppingStoreServiceImpl -> Обновлен товар: {}", updatedProduct);
        return mapper.toDto(updatedProduct);
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        Product product = findProductById(productId);
        log.info("ShoppingStoreServiceImpl -> Получены сведения о товаре: {}", product);
        return mapper.toDto(product);
    }

    @Override
    public Page<ProductDto> getProducts(ProductCategory productCategory, Pageable pageable) {
        log.info("ShoppingStoreServiceImpl -> Получение списка товаров по типу: {}", productCategory);


        Sort sort = Sort.by(Sort.DEFAULT_DIRECTION, String.join(",", pageable.getSort()));
        PageRequest pageRequest = PageRequest.of(pageable.getPage(), pageable.getSize(), sort);

        Page<ProductDto> products = repository.findAllByProductCategory(productCategory, pageRequest)
                .map(mapper::toDto);

        log.info("ShoppingStoreServiceImpl -> Получен список товаров по типу: {}", products);
        return products;
    }

    @Override
    @Transactional
    public boolean deleteProduct(UUID productId) {
        log.info("ShoppingStoreServiceImpl -> Удаление товара с id: {} из ассортимента", productId);
        Product product = findProductById(productId);
        product.setProductState(ProductState.DEACTIVATE);
        repository.save(product);
        log.info("ShoppingStoreServiceImpl ->  Из ассортимента удален товар: {}", product);
        return true;
    }

    @Override
    @Transactional
    public boolean setQuantityState(SetProductQuantityStateRequest request) {
        log.info("ShoppingStoreServiceImpl -> Установка статуса: {}", request);
        Product product = findProductById(request.getProductId());
        product.setQuantityState(request.getQuantityState());
        repository.save(product);
        log.info("ShoppingStoreServiceImpl -> Установлен статус: {}", request.getQuantityState());
        return true;
    }

    private Product findProductById(UUID productId) {
        return repository.findById(productId).orElseThrow(
                () -> new ProductNotFoundException(productId)
        );
    }

    private void updateProductContent(final Product target, final ProductDto source) {
        target.setProductName(
                source.getProductName() != null ? source.getProductName() : target.getProductName());
        target.setDescription(
                source.getDescription() != null ? source.getDescription() : target.getDescription());
        target.setImageSrc(source.getImageSrc() != null ? source.getImageSrc() : target.getImageSrc());
        target.setQuantityState(
                source.getQuantityState() != null ? source.getQuantityState() : target.getQuantityState());
        target.setProductState(
                source.getProductState() != null ? source.getProductState() : target.getProductState());
        target.setProductCategory(source.getProductCategory() != null ? source.getProductCategory()
                : target.getProductCategory());
        target.setPrice(source.getPrice() != null ? source.getPrice() : target.getPrice());
    }
}
