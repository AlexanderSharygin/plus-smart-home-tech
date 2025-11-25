package ru.yandex.practicum.commerce.delivery.model;

import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.dto.AddressDto;
import ru.yandex.practicum.interaction.dto.DeliveryDto;

import java.util.Objects;

@Component
public class DeliveryMapper {

    public Delivery toEntity(final DeliveryDto deliveryDto) {
        Objects.requireNonNull(deliveryDto);
        return Delivery.builder()
                .orderId(deliveryDto.getOrderId())
                .deliveryState(deliveryDto.getDeliveryState())
                .fromAddress(addressToEntity(deliveryDto.getFromAddress()))
                .toAddress(addressToEntity(deliveryDto.getToAddress()))
                .build();
    }

    public DeliveryDto toDto(Delivery delivery) {
        Objects.requireNonNull(delivery);
        return DeliveryDto.builder()
                .deliveryId(delivery.getDeliveryId())
                .orderId(delivery.getOrderId())
                .deliveryState(delivery.getDeliveryState())
                .fromAddress(addressToDto(delivery.getFromAddress()))
                .toAddress(addressToDto(delivery.getToAddress()))
                .build();
    }

    public Address addressToEntity(AddressDto addressDto) {
        return Address.builder()
                .country(addressDto.country())
                .city(addressDto.city())
                .street(addressDto.street())
                .house(addressDto.house())
                .flat(addressDto.flat())
                .build();
    }

    public AddressDto addressToDto(final Address address) {
        return new AddressDto(address.getCountry(), address.getCity(), address.getStreet(), address.getHouse(),
                address.getFlat());
    }
}
