package ru.yandex.practicum.commerce.delivery.model;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.dto.AddressDto;
import ru.yandex.practicum.interaction.dto.DeliveryDto;

import java.util.Objects;

@Component
public class DeliveryMapper {

    public Delivery toEntity(final DeliveryDto deliveryDto) {
        Address addressFrom = addressToEntity(deliveryDto.fromAddress());
        Address addressTo = addressToEntity(deliveryDto.toAddress());
        return new Delivery(deliveryDto.deliveryId(), null, null, null,
                addressFrom, addressTo, deliveryDto.deliveryState(), deliveryDto.orderId());
    }

    public DeliveryDto toDto(Delivery delivery) {
        Objects.requireNonNull(delivery);
        AddressDto addressDtoFrom = addressToDto(delivery.getFromAddress());
        AddressDto addressDtoTo = addressToDto(delivery.getToAddress());

        return new DeliveryDto(delivery.getDeliveryId(), addressDtoFrom, addressDtoTo,
                delivery.getOrderId(), delivery.getDeliveryState());
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
