package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.exception.dto.ErrorDto;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.info(LogError.UNEXPECTED_ERROR.getMessage());
        return new ErrorDto(LogError.UNEXPECTED_ERROR.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleThrowable(final Throwable e) {
        log.info(LogError.UNEXPECTED_ERROR.getMessage());
        return new ErrorDto(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleIncorrectValidException(final ConstraintViolationException e) {
        log.info(LogError.INCORRECT_REQUEST.getMessage());
        return new ErrorDto(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleNotFoundException(final NotFoundException e) {
        log.info(LogError.OBJECT_NOT_FOUND.getMessage());
        return new ErrorDto(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleNotAvailableException(NotAvailableException e) {
        log.info(LogError.INCORRECT_REQUEST.getMessage());
        return new ErrorDto(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleIncorrectValidException(final ValidationException e) {
        log.info(LogError.INCORRECT_REQUEST.getMessage());
        return new ErrorDto(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handlerAccessException(final OperationAccessException e) {
        log.info(LogError.OBJECT_NOT_FOUND.getMessage());
        return new ErrorDto(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleBadRequestException(final BadRequestException e) {
        log.info(LogError.INCORRECT_REQUEST.getMessage());
        return new ErrorDto(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleUnknownDataException(TimeDataException e) {
        log.info(LogError.INCORRECT_REQUEST.getMessage());
        return new ErrorDto(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleAlreadyExistException(final AlreadyExistsException e) {
        log.info(LogError.OBJECT_ALREADY_EXIST.getMessage());
        return new ErrorDto(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleException(final MethodArgumentNotValidException e) {
        log.info(LogError.INCORRECT_REQUEST.getMessage());
        return new ErrorDto(e.getMessage());
    }

}