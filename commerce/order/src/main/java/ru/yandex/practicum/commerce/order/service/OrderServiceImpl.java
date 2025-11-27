package ru.yandex.practicum.commerce.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.order.model.Order;
import ru.yandex.practicum.commerce.order.model.OrderMapper;
import ru.yandex.practicum.commerce.order.repository.OrderRepository;
import ru.yandex.practicum.interaction.dto.*;
import ru.yandex.practicum.interaction.enums.DeliveryState;
import ru.yandex.practicum.interaction.enums.OrderState;
import ru.yandex.practicum.interaction.exception.model.BadRequestException;
import ru.yandex.practicum.interaction.exception.model.NotFoundException;
import ru.yandex.practicum.interaction.feign.DeliveryFeignClient;
import ru.yandex.practicum.interaction.feign.PaymentFeignClient;
import ru.yandex.practicum.interaction.feign.ShoppingCartFeignClient;
import ru.yandex.practicum.interaction.feign.WarehouseFeignClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final ShoppingCartFeignClient cartClient;
    private final WarehouseFeignClient warehouseClient;
    private final PaymentFeignClient paymentClient;
    private final DeliveryFeignClient deliveryFeignClient;


    @Override
    public Page<OrderDto> getUserOrders(String username, Pageable pageable) {
        if (username.isEmpty()) {
            throw new BadRequestException("Username is empty");
        }
        ShoppingCartDto userCart = cartClient.getCart(username);
        Pageable newPageable;
        if (pageable.sort().getFirst().equals("productName")) {
            newPageable = new Pageable(pageable.page(), pageable.size(), List.of("state"));
        } else {
            newPageable = new Pageable(pageable.page(), pageable.size(), pageable.sort());
        }

        Sort sort = Sort.by(Sort.DEFAULT_DIRECTION, String.join(",", newPageable.sort()));
        PageRequest pageRequest = PageRequest.of(newPageable.page(), newPageable.size(), sort);

        Page<OrderDto> orders = repository.getAllOrdersByCartId(userCart.shoppingCartId(), pageRequest)
                .map(mapper::toDto);

        log.info("OrderService -> Получен список заказов: {}", orders);

        return orders;
    }

    @Override
    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        log.info("OrderService -> Создание заказа: {}", request);

        // Корзина пользователя
        ShoppingCartDto cart = request.shoppingCart();

        Order order = Order.builder()
                .state(OrderState.NEW)
                .products(cart.products())
                .cartId(cart.shoppingCartId())
                .build();

        Order newOrder = repository.save(order);

        // Собираем товары к заказу для подготовки к отправке
        AssemblyProductsForOrderRequest assemblyProducts = new AssemblyProductsForOrderRequest(cart.products(),
                newOrder.getOrderId());

        BookedProductsDto booking = warehouseClient.assemblyForOrder(assemblyProducts);

        newOrder.setDeliveryVolume(booking.deliveryVolume());
        newOrder.setDeliveryWeight(booking.deliveryWeight());
        newOrder.setFragile(booking.fragile());

        // Создание доставки
        DeliveryDto delivery = new DeliveryDto(UUID.randomUUID(), warehouseClient.getWarehouseAddress(),
                request.deliveryAddress(), newOrder.getOrderId(), DeliveryState.CREATED);

        DeliveryDto savedDelivery = deliveryFeignClient.planDelivery(delivery);
        newOrder.setDeliveryId(savedDelivery.deliveryId());

        // Формирование оплаты для заказа
        PaymentDto payment = paymentClient.payment(mapper.toDto(newOrder));
        newOrder.setPaymentId(payment.paymentId());

        // Расчёт стоимости товаров в заказе
        BigDecimal productPrice = paymentClient.productCost(mapper.toDto(newOrder));
        newOrder.setProductPrice(productPrice);

        Order savedOrder = repository.save(newOrder);
        OrderDto dto = mapper.toDto(savedOrder);

        log.info("OrderService -> Оформленный заказ: {}", dto);
        return dto;
    }

    @Override
    @Transactional
    public OrderDto productReturn(ProductReturnRequest request) {
        log.info("OrderService -> Запрос на возврат заказа: {}", request);

        Order order = getOrderById(request.orderId());
        warehouseClient.acceptReturn(request.products());
        order.setState(OrderState.PRODUCT_RETURNED);
        OrderDto dto = mapper.toDto(repository.save(order));

        log.info("OrderService -> Заказ пользователя после сборки: {}", dto);
        return dto;
    }

    @Override
    @Transactional
    public OrderDto payment(UUID orderId) {
        log.info("OrderService -> Оплата заказа с id: {}", orderId);

        Order order = getOrderById(orderId);
        order.setState(OrderState.PAID);
        OrderDto dto = mapper.toDto(repository.save(order));

        log.info("OrderService -> Заказ пользователя после оплаты: {}", dto);
        return dto;
    }

    @Override
    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        log.info("OrderController -> Оплата заказа  с id: {} произошла с ошибкой!", orderId);

        Order order = getOrderById(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        OrderDto dto = mapper.toDto(repository.save(order));

        log.info("OrderController -> Заказ пользователя после ошибки оплаты: {}", dto);
        return dto;
    }

    @Override
    @Transactional
    public OrderDto delivery(UUID orderId) {
        log.info("OrderService -> Доставка заказа с id: {}!", orderId);

        Order order = getOrderById(orderId);
        order.setState(OrderState.DELIVERED);
        OrderDto dto = mapper.toDto(repository.save(order));

        log.info("OrderService -> Заказ пользователя после доставки: {}", dto);
        return dto;
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        log.info("OrderController -> Доставка заказа с id: {} произошла с ошибкой!", orderId);

        Order order = getOrderById(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        OrderDto dto = mapper.toDto(repository.save(order));

        log.info("OrderController -> Заказ пользователя после ошибки доставки: {}", dto);
        return dto;
    }

    @Override
    @Transactional
    public OrderDto complete(UUID orderId) {
        log.info("OrderController -> Завершение заказа с id: {}!", orderId);

        Order order = getOrderById(orderId);
        order.setState(OrderState.COMPLETED);
        OrderDto dto = mapper.toDto(repository.save(order));

        log.info("OrderController -> Заказ пользователя после всех стадий и завершенный: {}", dto);
        return dto;
    }

    @Override
    @Transactional
    public OrderDto calculateTotal(UUID orderId) {
        log.info("OrderController -> Расчёт стоимости заказа с id: {}!", orderId);

        Order order = getOrderById(orderId);
        BigDecimal totalCost = paymentClient.getTotalCost(mapper.toDto(order));
        order.setTotalPrice(totalCost);
        OrderDto dto = mapper.toDto(repository.save(order));

        log.info("OrderController -> Заказ пользователя с расчётом общей стоимости: {}", dto);
        return dto;
    }

    @Override
    @Transactional
    public OrderDto calculateDelivery(UUID orderId) {
        log.info("OrderController -> Расчёт стоимости доставки заказа с id: {}!", orderId);

        Order order = getOrderById(orderId);
        BigDecimal deliveryPrice = deliveryFeignClient.deliveryCost(mapper.toDto(order));
        order.setDeliveryPrice(deliveryPrice);
        OrderDto dto = mapper.toDto(repository.save(order));

        log.info("OrderController -> Заказ пользователя с расчётом доставки: {}", dto);
        return dto;
    }

    @Override
    @Transactional
    public OrderDto assembly(UUID orderId) {
        log.info("OrderController -> Сборка заказа с id: {}!", orderId);

        Order order = getOrderById(orderId);
        order.setState(OrderState.ASSEMBLED);
        OrderDto dto = mapper.toDto(repository.save(order));

        log.info("OrderController -> Заказ пользователя после сборки: {}", dto);
        return dto;
    }

    @Override
    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        log.info("OrderController -> Сборка заказа с id - {}, произошла с ошибкой!", orderId);

        Order order = getOrderById(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        OrderDto dto = mapper.toDto(repository.save(order));

        log.info("OrderController -> Заказ пользователя после ошибки сборки: {}", order);
        return dto;
    }

    private Order getOrderById(UUID orderId) {
        return repository.findOrderByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Не найден заказ c id: " + orderId));
    }
}
