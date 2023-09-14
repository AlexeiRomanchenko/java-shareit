package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.handler.LogError;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeDataExceptionTest {
    @Test
    public void timeDataExceptionTest() {
        String errorMessage = LogError.INCORRECT_REQUEST.getMessage();
        TimeDataException exception = new TimeDataException(errorMessage);
        assertEquals(errorMessage, exception.getMessage());
    }

}