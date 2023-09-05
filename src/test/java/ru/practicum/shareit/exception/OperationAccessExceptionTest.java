package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.handler.LogError;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OperationAccessExceptionTest {
    @Test
    public void operationAccessExceptionTest() {
        String errorMessage = LogError.OBJECT_NOT_FOUND.getMessage();
        OperationAccessException exception = new OperationAccessException(errorMessage);
        assertEquals(errorMessage, exception.getMessage());
    }

}