package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.handler.LogError;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NotFoundExceptionTest {
    @Test
    public void notFoundExceptionTest() {
        String errorMessage = LogError.OBJECT_NOT_FOUND.getMessage();
        NotFoundException exception = new NotFoundException(errorMessage);
        assertEquals(errorMessage, exception.getMessage());
    }

}