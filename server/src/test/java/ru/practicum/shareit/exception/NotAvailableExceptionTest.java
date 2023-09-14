package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.handler.LogError;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NotAvailableExceptionTest {
    @Test
    public void notAvailableExceptionTest() {
        String errorMessage = LogError.INCORRECT_REQUEST.getMessage();
        NotAvailableException exception = new NotAvailableException(errorMessage);
        assertEquals(errorMessage, exception.getMessage());
    }

}