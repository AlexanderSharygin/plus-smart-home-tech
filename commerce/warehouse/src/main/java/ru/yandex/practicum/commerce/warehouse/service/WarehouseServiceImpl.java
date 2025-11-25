package ru.yandex.practicum.commerce.warehouse.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.warehouse.entity.Address;
import ru.yandex.practicum.commerce.warehouse.entity.OrderBooking;
import ru.yandex.practicum.commerce.warehouse.entity.Product;
import ru.yandex.practicum.commerce.warehouse.entity.ProductMapper;
import ru.yandex.practicum.commerce.warehouse.repository.BookingRepository;
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
    private final BookingRepository bookingRepository;
    private final ProductMapper mapper;

    @Override
    @Transactional
    public void addNewProductToWarehouse(NewInWarehouseRequest request) {
        if (repository.existsById(request.productId())) {
            throw new ConflictException("Товар уже зарегистрирован на складе " + request);
        }
        repository.save(mapper.toEntity(request));
        log.info("Добавлен новый товар на склад");
    }

    @Override
    public BookedProductsDto checkProductAvailability(ShoppingCartDto cart) {
        double weight = 0;
        double volume = 0;
        boolean fragile = false;

        Map<UUID, Long> cartProducts = cart.products();
        Map<UUID, Product> products = repository.findAllById(cartProducts.keySet())
                .stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));

        for (Map.Entry<UUID, Long> cartProduct : cartProducts.entrySet()) {
            Product product = products.get(cartProduct.getKey());
            if (cartProduct.getValue() > product.getQuantity()) {
                throw new ConflictException("Товара с id: " + product.getProductId() + " в наличии, чем в корзине!");
            }

            double productVolume = product.getDimension().getHeight() * product.getDimension().getDepth() *
                            product.getDimension().getWidth();
            volume += productVolume * cartProduct.getValue();
            weight += product.getWeight() * cartProduct.getValue();
            if (product.getFragile() == true) {
                fragile = true;
            }
        }

        return new BookedProductsDto(weight, volume, fragile);
    }

    @Override
    @Transactional
    public void takeProductToWarehouse(AddToWarehouseRequest request) {
        Product product = repository.findById(request.productId())
                .orElseThrow(() -> new NotFoundException("Продукт с id " + request.productId() + " не найден"));
        Long quantity = product.getQuantity();
        if (quantity == null) {
            quantity = 0L;
        }
        product.setQuantity(quantity + request.quantity());
        repository.save(product);
        log.info("Изменено количество товара: {}", product.getQuantity());
    }

    @Override
    public AddressDto getWarehouseAddress() {
        String address = new Address().getAddress();
        AddressDto dto = new AddressDto(address, address, address, address, address);
        log.info("Найден адрес склада: {}", dto);
        return dto;
    }

    @Override
    @Transactional
    public BookedProductsDto assemblyProductsForOrder(@RequestBody @Valid AssemblyProductsForOrderRequest request) {
        log.info("WarehouseServiceImpl: -> Собираем товары к заказу для подготовки к отправке: {}", request);

        double weight = 0;
        double volume = 0;
        boolean fragile = false;

        UUID orderId = request.getOrderId();

        Map<UUID, Long> productsForBooking = request.getProducts();
        Map<UUID, Product> products = repository.findAllById(productsForBooking.keySet())
                .stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));

        for (Map.Entry<UUID, Long> cartProduct : productsForBooking.entrySet()) {
            Product product = products.get(cartProduct.getKey());
            if (cartProduct.getValue() > product.getQuantity()) {
                log.info("Товара с id: {}, на складе меньше, чем в заказе!", product.getProductId());
                throw new ConflictException("Товара на складе меньше, чем в заказе!"+ product.getProductId());
            }
            product.setQuantity(product.getQuantity() - cartProduct.getValue());

            var productVolume =
                    product.getDimension().getHeight() *
                            product.getDimension().getDepth() *
                            product.getDimension().getWidth();

            volume += productVolume * cartProduct.getValue();
            weight += product.getWeight() * cartProduct.getValue();

            if (product.getFragile() == true) {
                fragile = true;
            }
        }

        repository.saveAll(products.values());

        BookedProductsDto dto = new BookedProductsDto(weight, volume, fragile);

        OrderBooking booking = OrderBooking.builder().build();
        booking.setOrderId(orderId);
        booking.setProducts(productsForBooking);

        bookingRepository.save(booking);

        log.info("WarehouseServiceImpl: -> Товары собраны и готовы к отправке!");
        return dto;
    }

    @Override
    public void acceptReturn(@RequestBody Map<UUID, Integer> products) {
        log.info("WarehouseServiceImpl: -> Возврат товаров на склад: {}", products);

        products.forEach((key, value) -> {
            AddToWarehouseRequest request = new AddToWarehouseRequest(key, value);

            takeProductToWarehouse(request);
        });

        log.info("WarehouseServiceImpl: -> Товары приняты на склад!");
    }
}