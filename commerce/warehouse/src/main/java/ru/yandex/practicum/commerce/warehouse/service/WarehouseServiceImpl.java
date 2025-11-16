package ru.yandex.practicum.commerce.warehouse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.warehouse.entity.Address;
import ru.yandex.practicum.commerce.warehouse.entity.Product;
import ru.yandex.practicum.commerce.warehouse.entity.ProductMapper;
import ru.yandex.practicum.commerce.warehouse.repository.ProductRepository;
import ru.yandex.practicum.interaction.dto.*;
import ru.yandex.practicum.interaction.exception.model.ConflictException;
import ru.yandex.practicum.interaction.exception.model.NotFoundException;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Override
    @Transactional
    public void addNewProductToWarehouse(NewInWarehouseRequest request) {
        if (repository.existsById(request.getProductId())) {
            log.error("Товар уже зарегистрирован на складе - {}", request);
            throw new ConflictException("Товар уже зарегистрирован на складе " + request);
        }
        repository.save(mapper.toEntity(request));
        log.info("WarehouseServiceImpl -> Добавлен новый товар на склад");
    }

    @Override
    public BookedProductsDto checkProductAvailability(ShoppingCartDto cart) {
        double weight = 0;
        double volume = 0;
        boolean fragile = false;

        Map<UUID, Long> cartProducts = cart.getProducts();
        Map<UUID, Product> products = repository.findAllById(cartProducts.keySet())
                .stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));

        for (Map.Entry<UUID, Long> cartProduct : cartProducts.entrySet()) {
            Product product = products.get(cartProduct.getKey());
            if (cartProduct.getValue() > product.getQuantity()) {
                log.info("Товара с id: {}, на складе меньше, чем в корзине!", product.getProductId());
                throw new ConflictException("Товара с id: " + product.getProductId() +
                        " на складе меньше, чем в корзине!");
            }

            double productVolume =
                    product.getDimension().getHeight() *
                            product.getDimension().getDepth() *
                            product.getDimension().getWidth();

            volume += productVolume * cartProduct.getValue();
            weight += product.getWeight() * cartProduct.getValue();

            if (product.getFragile() == true) {
                fragile = true;
            }
        }

        BookedProductsDto dto = BookedProductsDto.builder()
                .deliveryVolume(volume)
                .deliveryWeight(weight)
                .fragile(fragile)
                .build();

        log.info("WarehouseServiceImpl -> Количество товаров на складе для корзины: {} проверено", cart);
        log.info("WarehouseServiceImpl -> Объем и вес заказа: {}", dto);
        return dto;
    }

    @Override
    @Transactional
    public void takeProductToWarehouse(AddToWarehouseRequest request) {
        log.info("WarehouseServiceImpl -> Добавление товара в количестве: {}", request.getQuantity());

        Product product = repository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Продукт с id " + request.getProductId() + " не найден"));

        Long quantity = product.getQuantity();

        if (quantity == null) {
            quantity = 0L;
        }

        product.setQuantity(quantity + request.getQuantity());
        repository.save(product);

        log.info("WarehouseServiceImpl -> Новое количество товара: {}", product.getQuantity());
    }

    @Override
    public AddressDto getWarehouseAddress() {
        log.info("WarehouseServiceImpl -> Получение адреса склада для расчёта доставки");
        String address = new Address().getAddress();
        AddressDto dto = AddressDto.builder()
                .country(address)
                .city(address)
                .street(address)
                .house(address)
                .flat(address)
                .build();
        log.info("WarehouseServiceImpl -> Актуальный адрес склада: {}", dto);
        return dto;
    }
}