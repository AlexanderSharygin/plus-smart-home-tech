package ru.yandex.practicum.commerce.warehouse.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.security.SecureRandom;
import java.util.Random;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {
    String country;
    String city;
    String street;
    String house;
    String flat;

    private static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};

    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)];
    
    public String getAddress() {
        return CURRENT_ADDRESS;
    }
}