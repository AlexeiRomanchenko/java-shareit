package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.handler.LogError;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BadRequestExceptionTest {
    @Test
    public void badRequestExceptionTest() {
        String errorMessage = LogError.INCORRECT_REQUEST.getMessage();
        BadRequestException exception = new BadRequestException(errorMessage);
        assertEquals(errorMessage, exception.getMessage());
    }

}