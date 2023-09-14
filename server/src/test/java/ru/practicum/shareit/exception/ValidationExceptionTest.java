package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.handler.LogError;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidationExceptionTest {
    @Test
    public void validationExceptionTest() {
        String errorMessage = LogError.INCORRECT_REQUEST.getMessage();
        ValidationException exception = new ValidationException(errorMessage);
        assertEquals(errorMessage, exception.getMessage());
    }

}