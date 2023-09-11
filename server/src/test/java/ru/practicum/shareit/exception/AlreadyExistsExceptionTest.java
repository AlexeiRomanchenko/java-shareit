package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.handler.LogError;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AlreadyExistsExceptionTest {
    @Test
    public void alreadyExistsExceptionTest() {
        String errorMessage = LogError.OBJECT_ALREADY_EXIST.getMessage();
        AlreadyExistsException exception = new AlreadyExistsException(errorMessage);
        assertEquals(errorMessage, exception.getMessage());
    }

}