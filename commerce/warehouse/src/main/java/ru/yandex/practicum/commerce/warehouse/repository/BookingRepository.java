package ru.yandex.practicum.commerce.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.commerce.warehouse.entity.OrderBooking;

import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<OrderBooking, UUID> {

    Optional<OrderBooking> findBookingByOrderId(UUID orderId);

}