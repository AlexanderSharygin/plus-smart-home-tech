package ru.yandex.practicum.commerce.warehouse.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public void addNewProduct(NewInWarehouseRequest request) {
        log.info("Добавление товара на склад: {}", request);
        service.addNewProductToWarehouse(request);
    }

    @Override
    public BookedProductsDto checkProductAvailability(ShoppingCartDto cart) {
        log.info("Проверка количества товаров на складе: {}", cart);
        return service.checkProductAvailability(cart);
    }

    @Override
    public void addToWarehouse(AddToWarehouseRequest request) {
        log.info("Прием товара на склад: {}", request);
        service.takeProductToWarehouse(request);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        log.info("Получение адреса склада");
        return service.getWarehouseAddress();
    }

    @Override
    public void acceptReturn(Map<UUID, Integer> products) {
        log.info("WarehouseController: -> Принимаем возврат товаров на склад: {}", products);
        service.acceptReturn(products);
    }

    @Override
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        log.info("WarehouseController: -> Собираем товары к заказу для подготовки к отправке: {}", request);
        BookedProductsDto products = service.assemblyProductsForOrder(request);
        return products;
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        log.info("WarehouseServiceImpl: -> Передача товаров в доставку: {}", request);

        UUID orderId = request.getOrderId();

        OrderBooking booking = bookingRepository.findBookingByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено!"));

        booking.setDeliveryId(request.getDeliveryId());
        bookingRepository.save(booking);

        log.info("WarehouseServiceImpl: -> Товары переданы в доставку");
    }
}