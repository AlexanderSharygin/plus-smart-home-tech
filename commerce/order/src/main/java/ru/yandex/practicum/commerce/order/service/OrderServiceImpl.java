package ru.yandex.practicum.commerce.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.order.model.Order;
import ru.yandex.practicum.commerce.order.model.OrderMapper;
import ru.yandex.practicum.commerce.order.repository.OrderRepository;
import ru.yandex.practicum.interaction.dto.*;
import ru.yandex.practicum.interaction.enums.DeliveryState;
import ru.yandex.practicum.interaction.enums.OrderState;
import ru.yandex.practicum.interaction.exception.model.NotFoundException;
import ru.yandex.practicum.interaction.feign.DeliveryFeignClient;
import ru.yandex.practicum.interaction.feign.PaymentFeignClient;
import ru.yandex.practicum.interaction.feign.ShoppingCartFeignClient;
import ru.yandex.practicum.interaction.feign.WarehouseFeignClient;

import java.math.BigDecimal;
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
    public Page<OrderDto> getOrders(String username, Pageable pageable) {
        ShoppingCartDto userCart = cartClient.getCart(username);
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        Page<OrderDto> orders = repository.getAllOrdersByCartId(userCart.shoppingCartId(), pageRequest)
                .map(mapper::toDto);
        log.info("Найден список заказов: {}", orders);

        return orders;
    }

    @Override
    @Transactional
    public OrderDto createOrder(CreateNewOrderRequest request) {
        ShoppingCartDto cart = request.shoppingCart();
        Order order = Order.builder()
                .state(OrderState.NEW)
                .products(cart.products())
                .cartId(cart.shoppingCartId())
                .build();
        Order newOrder = repository.save(order);

        AssemblyProductsForOrderRequest assemblyProducts = new AssemblyProductsForOrderRequest(cart.products(),
                newOrder.getOrderId());
        BookedProductsDto booking = warehouseClient.assemblyForOrder(assemblyProducts);
        newOrder.setDeliveryVolume(booking.deliveryVolume());
        newOrder.setDeliveryWeight(booking.deliveryWeight());
        newOrder.setFragile(booking.fragile());

        DeliveryDto delivery = new DeliveryDto(UUID.randomUUID(), warehouseClient.getWarehouseAddress(),
                request.deliveryAddress(), newOrder.getOrderId(), DeliveryState.CREATED);

        DeliveryDto savedDelivery = deliveryFeignClient.createDelivery(delivery);
        newOrder.setDeliveryId(savedDelivery.deliveryId());
        PaymentDto payment = paymentClient.payment(mapper.toDto(newOrder));
        newOrder.setPaymentId(payment.paymentId());
        BigDecimal productPrice = paymentClient.productCost(mapper.toDto(newOrder));
        newOrder.setProductPrice(productPrice);
        Order savedOrder = repository.save(newOrder);
        OrderDto orderDto = mapper.toDto(savedOrder);

        log.info("Успешно оформлен заказ: {}", orderDto);
        return orderDto;
    }

    @Override
    @Transactional
    public OrderDto returnProduct(ProductReturnRequest request) {
        Order order = getOrderById(request.orderId());
        warehouseClient.acceptReturn(request.products());
        order.setState(OrderState.PRODUCT_RETURNED);
        OrderDto orderDto = mapper.toDto(repository.save(order));
        log.info("Товар успешно возвращен");

        return orderDto;
    }

    @Override
    @Transactional
    public OrderDto payment(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.PAID);
        OrderDto orderDto = mapper.toDto(repository.save(order));
        log.info("Заказ с id {} оплачен", orderId);

        return orderDto;
    }

    @Override
    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        OrderDto orderDto = mapper.toDto(repository.save(order));
        log.info("Ошибка оплаты заказа с id: {}", orderId);

        return orderDto;
    }

    @Override
    @Transactional
    public OrderDto delivery(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.DELIVERED);
        OrderDto orderDto = mapper.toDto(repository.save(order));
        log.info("Выполняется доставка заказа с id: {}", orderId);

        return orderDto;
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        OrderDto orderDto = mapper.toDto(repository.save(order));
        log.info("Ошибка при доставке товара с id: {}", orderId);

        return orderDto;
    }

    @Override
    @Transactional
    public OrderDto complete(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.COMPLETED);
        OrderDto orderDto = mapper.toDto(repository.save(order));
        log.info("Заказ c id {} завершен", orderId);

        return orderDto;
    }

    @Override
    @Transactional
    public OrderDto calculateTotal(UUID orderId) {
        Order order = getOrderById(orderId);
        BigDecimal totalCost = paymentClient.getTotalCost(mapper.toDto(order));
        order.setTotalPrice(totalCost);
        OrderDto orderDto = mapper.toDto(repository.save(order));
        log.info("Стоимость заказа с id {} = {}", orderId, totalCost);

        return orderDto;
    }

    @Override
    @Transactional
    public OrderDto calculateDelivery(UUID orderId) {
        Order order = getOrderById(orderId);
        BigDecimal deliveryPrice = deliveryFeignClient.getDeliveryCost(mapper.toDto(order));
        order.setDeliveryPrice(deliveryPrice);
        OrderDto orderDto = mapper.toDto(repository.save(order));
        log.info("Стоимость доставки заказа с id {} = {}", orderId, deliveryPrice);

        return orderDto;
    }

    @Override
    @Transactional
    public OrderDto assembly(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.ASSEMBLED);
        OrderDto orderDto = mapper.toDto(repository.save(order));
        log.info("Успешно собран заказ с id: {}", orderId);

        return orderDto;
    }

    @Override
    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        OrderDto orderDto = mapper.toDto(repository.save(order));
        log.info("Ошибка при сборке заказа с id {}", orderId);

        return orderDto;
    }

    private Order getOrderById(UUID orderId) {
        return repository.findOrderByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Не найден заказ c id: " + orderId));
    }
}