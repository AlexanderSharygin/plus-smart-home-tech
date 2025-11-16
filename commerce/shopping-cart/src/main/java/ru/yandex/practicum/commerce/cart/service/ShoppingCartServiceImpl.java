package ru.yandex.practicum.commerce.cart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.cart.model.ShoppingCart;
import ru.yandex.practicum.commerce.cart.model.ShoppingCartMapper;
import ru.yandex.practicum.commerce.cart.repository.CartRepository;
import ru.yandex.practicum.interaction.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.dto.ShoppingCartDto;
import ru.yandex.practicum.interaction.exception.model.BadRequestException;
import ru.yandex.practicum.interaction.exception.model.ConflictException;
import ru.yandex.practicum.interaction.feign.WarehouseFeignClient;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final CartRepository repository;
    private final ShoppingCartMapper mapper;
    private final WarehouseFeignClient warehouse;

    @Override
    public ShoppingCartDto getCart(String username) {
        ShoppingCart cart = getShoppingCartByUser(username);
        log.info("Найдена корзина: {}", cart);

        return mapper.toDto(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDto addProduct(String username, Map<UUID, Long> request) {
        ShoppingCart cart = getShoppingCartByUser(username);
        if (cart.getActive() != false) {
            cart.setProducts(request);
            warehouse.checkProductAvailability(mapper.toDto(cart));
            repository.save(cart);
            log.info("Товар добавлен в корзину: {}", cart);
        }
        return mapper.toDto(cart);
    }

    @Override
    @Transactional
    public void deactivatingCart(String username) {
        ShoppingCart cart = getShoppingCartByUser(username);
        cart.setActive(false);
        repository.save(cart);
        log.info("Корзина пользователя {} деактивирована", username);
    }

    @Override
    public ShoppingCartDto removeProduct(String username, List<UUID> productIds) {
        ShoppingCart cart = getShoppingCartByUser(username);
        if (cart.getActive() == false) {
            throw new ConflictException("Нельзя удалить продукт из неактивной корзины");
        }

        if (!cart.getProducts().keySet().containsAll(productIds)) {
            throw new ConflictException("Нельзя удалить продукт из пустой корзины");
        }

        productIds.forEach(productId -> cart.getProducts().remove(productId));

        ShoppingCart savedCart = repository.save(cart);
        log.info("Товары удалены из корзины: {}", savedCart);
        return mapper.toDto(savedCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request) {
        ShoppingCart cart = getShoppingCartByUser(username);
        cart.getProducts().put(request.productId(), request.newQuantity());
        ShoppingCart savedCart = repository.save(cart);
        log.info("Количества товаров в корзине изменено: {}", savedCart);
        return mapper.toDto(savedCart);
    }

    private ShoppingCart getShoppingCartByUser(String username) {
        if (username == null || username.isEmpty()) {
            throw new BadRequestException("Недопустимое имя пользователя");
        }
        Optional<ShoppingCart> cart = repository.findAllByUsername(username);
        if (cart.isEmpty()) {
            ShoppingCart newCart = ShoppingCart.builder().username(username).active(true).build();
            cart = Optional.of(repository.save(newCart));
        }
        return cart.get();
    }
}