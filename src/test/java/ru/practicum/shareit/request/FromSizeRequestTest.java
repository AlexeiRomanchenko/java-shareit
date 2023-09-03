package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.service.FromSizeRequest;

import static org.junit.jupiter.api.Assertions.*;

public class FromSizeRequestTest {
    Pageable pageable = new FromSizeRequest(10, 2, Sort.unsorted());

    @Test
    void shouldExceptionWhenSizeLessThanZero() {
        assertThrows(IllegalArgumentException.class, () -> new FromSizeRequest(-10, 2, Sort.unsorted()));
    }

    @Test
    void shouldExceptionWhenLimitLessThanZero() {
        assertThrows(IllegalArgumentException.class, () -> new FromSizeRequest(10, -2, Sort.unsorted()));
    }


    @Test
    void getPageNumber() {
        assertEquals(5, pageable.getPageNumber());
    }

    @Test
    void next() {
        assertEquals(6, pageable.next().getPageNumber());
    }

    @Test
    void previousOrFirst() {
        assertEquals(PageRequest.of(4, 2, Sort.unsorted()), pageable.previousOrFirst());
    }

    @Test
    void first() {
        assertEquals(PageRequest.of(0, 2, Sort.unsorted()), pageable.first());
    }

    @Test
    void hasPrevious() {
        assertTrue(pageable.hasPrevious());
    }

}