package ru.yandex.practicum.commerce.delivery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.delivery.model.Delivery;
import ru.yandex.practicum.commerce.delivery.model.DeliveryMapper;
import ru.yandex.practicum.commerce.delivery.repository.DeliveryRepository;
import ru.yandex.practicum.interaction.dto.DeliveryDto;
import ru.yandex.practicum.interaction.dto.OrderDto;
import ru.yandex.practicum.interaction.dto.ShippedToDeliveryRequest;
import ru.yandex.practicum.interaction.enums.DeliveryState;
import ru.yandex.practicum.interaction.exception.model.NotFoundException;
import ru.yandex.practicum.interaction.feign.OrderFeignClient;
import ru.yandex.practicum.interaction.feign.WarehouseFeignClient;


import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository repository;
    private final DeliveryMapper mapper;
    private final OrderFeignClient orderClient;
    private final WarehouseFeignClient warehouseClient;

    @Value("${delivery.base_cost}")
    private BigDecimal baseCost;

    @Value("${delivery.warehouse_address_1_ratio}")
    private BigDecimal warehouseAddress1Ratio;

    @Value("${delivery.warehouse_address_2_ratio}")
    private BigDecimal warehouseAddress2Ratio;

    @Value("${delivery.fragile_ratio}")
    private BigDecimal fragileRatio;

    @Value("${delivery.weight_ratio}")
    private BigDecimal weightRatio;

    @Value("${delivery.volume_ratio}")
    private BigDecimal volumeRatio;

    @Value("${delivery.delivery_address_ratio}")
    private BigDecimal deliveryAddressRatio;

    @Override
    @Transactional
    public DeliveryDto planDelivery(DeliveryDto dto) {
        Delivery delivery = mapper.toEntity(dto);
        delivery.setDeliveryState(DeliveryState.CREATED);
        DeliveryDto savedDelivery = mapper.toDto(repository.save(delivery));
        log.info("DeliveryService -> Указанная заявка с присвоенным идентификатором: {}",
                savedDelivery.deliveryId());
        return savedDelivery;
    }

    @Override
    @Transactional
    public void deliverySuccessful(UUID deliveryId) {
        Delivery delivery = getDeliveryById(deliveryId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        orderClient.delivery(delivery.getOrderId());
        Delivery updatedDelivery = repository.save(delivery);
        log.info("DeliveryService -> Успешная доставка заказа с id: {}", updatedDelivery.getDeliveryId());
    }

    @Override
    @Transactional
    public void deliveryPicked(UUID deliveryId) {
        Delivery delivery = getDeliveryById(deliveryId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        ShippedToDeliveryRequest request = new ShippedToDeliveryRequest(delivery.getOrderId(), deliveryId);
        warehouseClient.shippedToDelivery(request);
        Delivery updatedDelivery = repository.save(delivery);
        log.info("DeliveryService -> Товар получен в доставку с id: {}", updatedDelivery.getDeliveryId());
    }

    @Override
    @Transactional
    public void deliveryFailed(UUID deliveryId) {
        Delivery delivery = getDeliveryById(deliveryId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        orderClient.deliveryFailed(delivery.getOrderId());
        Delivery updatedDelivery = repository.save(delivery);
        log.info("DeliveryService -> Товар не получен в доставку с id: {}", updatedDelivery.getDeliveryId());
    }

    @Override
    @Transactional
    public BigDecimal deliveryCost(OrderDto order) {
        Delivery delivery = getDeliveryById(order.deliveryId());
        String fromAddressStreet = delivery.getFromAddress().getStreet();
        BigDecimal totalCost = getTotalCost(delivery, fromAddressStreet);
        delivery.setDeliveryWeight(BigDecimal.valueOf(order.deliveryWeight()));
        delivery.setDeliveryVolume(BigDecimal.valueOf(order.deliveryVolume()));
        delivery.setFragile(order.fragile());
        repository.save(delivery);
        log.info("DeliveryService -> Полная стоимость доставки заказа: {}", totalCost);
        return totalCost;
    }

    private BigDecimal getTotalCost(Delivery delivery, String fromAddressStreet) {
        String toAddressString = delivery.getToAddress().getStreet();
        BigDecimal totalCost = baseCost;
        if (!fromAddressStreet.equals("ADDRESS_2")) {
            totalCost = totalCost.add(baseCost.multiply(warehouseAddress1Ratio));
        } else {
            totalCost = totalCost.add(baseCost.multiply(warehouseAddress2Ratio));
        }
        if (delivery.getFragile().equals(Boolean.TRUE)) {
            totalCost = totalCost.add(totalCost.multiply(fragileRatio));
        }
        totalCost = totalCost.add(delivery.getDeliveryWeight().multiply(weightRatio));
        totalCost = totalCost.add(delivery.getDeliveryVolume().multiply(volumeRatio));
        if (!toAddressString.equals(fromAddressStreet)) {
            totalCost = totalCost.add(totalCost.multiply(deliveryAddressRatio));
        }
        return totalCost;
    }

    private Delivery getDeliveryById(UUID deliveryId) {
        return repository.findDeliveryByDeliveryId(deliveryId)
                .orElseThrow(() -> new NotFoundException("Не найдена доставка с id: " + deliveryId));
    }
}