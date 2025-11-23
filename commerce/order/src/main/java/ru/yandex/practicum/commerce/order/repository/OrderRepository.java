package ru.yandex.practicum.commerce.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.commerce.order.OrderService;
import ru.yandex.practicum.commerce.order.model.Order;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<OrderService> findOrderByOrderId(UUID orderId);

    Page<OrderService> getAllOrdersByCartId(UUID cartId, Pageable pageable);
}
