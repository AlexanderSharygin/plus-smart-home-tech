package ru.yandex.practicum.commerce.warehouse.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.warehouse.entity.OrderBooking;
import ru.yandex.practicum.commerce.warehouse.repository.BookingRepository;
import ru.yandex.practicum.commerce.warehouse.service.WarehouseService;
import ru.yandex.practicum.interaction.dto.*;
import ru.yandex.practicum.interaction.exception.model.NotFoundException;
import ru.yandex.practicum.interaction.feign.WarehouseFeignClient;

import java.util.Map;
import java.util.UUID;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController implements WarehouseFeignClient {

    private final WarehouseService service;
    private final BookingRepository bookingRepository;

    @Override
    public AddressDto getWarehouseAddress() {
        log.info("Получение адреса склада");
        return service.getWarehouseAddress();
    }

    @Override
    public void addNewProduct(NewInWarehouseRequest request) {
        log.info("Добавление товара на склад: {}", request);
        service.addNewProduct(request);
    }

    @Override
    public void addToWarehouse(AddToWarehouseRequest request) {
        log.info("Прием товара на склад: {}", request);
        service.addToWarehouse(request);
    }

    @Override
    public BookedProductsDto checkProductAvailability(ShoppingCartDto cart) {
        log.info("Проверка количества товаров на складе: {}", cart);
        return service.checkProductAvailability(cart);
    }

    @Override
    public void acceptReturn(Map<UUID, Integer> products) {
        log.info("Возврат товаров на склад: {}", products);
        service.acceptReturn(products);
    }

    @Override
    public BookedProductsDto assemblyForOrder(AssemblyProductsForOrderRequest request) {
        log.info("Начинаем сборку заказа: {}", request);
        return service.assemblyForOrder(request);
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        log.info("Передаем товары в доставку: {}", request);
        UUID orderId = request.orderId();
        OrderBooking booking = bookingRepository.findBookingByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено!"));
        booking.setDeliveryId(request.deliveryId());
        bookingRepository.save(booking);
    }
}