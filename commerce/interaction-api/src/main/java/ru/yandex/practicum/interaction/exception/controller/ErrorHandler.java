package ru.yandex.practicum.interaction.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.interaction.exception.model.ErrorResponse;
import ru.yandex.practicum.interaction.exception.model.ProductNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {


    @ExceptionHandler({
            ProductNotFoundException.class

    })
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handlerNotFoundException(final ProductNotFoundException e) {
        log.error("Ошибка: 404 NOT_FOUND - {}", Arrays.stream(e.getStackTrace()).toList());
        return ErrorResponse.builder()
                .httpStatus(NOT_FOUND)
                .userMessage(e.getMessage())
                .message("Ошибка: 404 NOT_FOUND")
                .localizedMessage(e.getLocalizedMessage())
                .build();
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameterException(final Throwable e) {
        log.warn("MissingServletRequestParameterException. Message: {}, StackTrace: {}", e.getMessage(), e.getStackTrace());

        return ErrorResponse.builder()
                .httpStatus(BAD_REQUEST)
                .userMessage(e.getMessage())
                .message("Validation error: 400 NOT_FOUND")
                .localizedMessage(e.getLocalizedMessage())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOtherExceptions(final Throwable e) {
        log.warn("Unknown error. Message: {}, StackTrace: {}", e.getMessage(), e.getStackTrace());

        return ErrorResponse.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .userMessage(e.getMessage())
                .message("Unknown error")
                .localizedMessage(e.getLocalizedMessage())
                .build();
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectParameterException(final BadRequestException e) {
        log.warn(e.getMessage());

        return ErrorResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .userMessage(e.getMessage())
                .message("Bad request")
                .localizedMessage(e.getLocalizedMessage())
                .build();
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse commonValidation(MethodArgumentNotValidException e) {
        List<FieldError> items = e.getBindingResult().getFieldErrors();
        String message = items.stream()
                .map(FieldError::getField)
                .findFirst()
                .orElse("Unknown error");
        Optional<String> title = items.stream()
                .map(FieldError::getDefaultMessage)
                .findFirst();
        if (title.isPresent()) {
            message = message + " - " + title.get();
        }
        log.warn(message);


        return ErrorResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .userMessage(message)
                .message("Validation error")
                .localizedMessage(e.getLocalizedMessage())
                .build();
    }
}

