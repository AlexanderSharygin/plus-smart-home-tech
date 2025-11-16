package ru.yandex.practicum.interaction.exception.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class EmptyShoppingCartException extends RuntimeException {

  public EmptyShoppingCartException(String message) {
    super(message);
  }
}
