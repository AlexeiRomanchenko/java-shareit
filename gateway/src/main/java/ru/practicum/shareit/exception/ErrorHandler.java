package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.exception.dto.ErrorDto;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("Произошла непредвиденная ошибка. {}. Stack trace: {}.",
                HttpStatus.BAD_REQUEST.value(),
                e.getStackTrace());
        return new ErrorDto("Unknown state: UNSUPPORTED_STATUS");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleThrowable(final Throwable e) {
        log.error("Произошла внутренняя ошибка сервера с кодом {}. Stack trace: {}.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getStackTrace());
        return new ErrorDto("Что-то пошло не так");
    }

}