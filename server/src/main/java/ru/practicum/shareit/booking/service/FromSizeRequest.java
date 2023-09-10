package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class FromSizeRequest extends PageRequest {
    private final int from;

    public FromSizeRequest(int from, int size, Sort sort) {
        super(from / size, size, sort);
        this.from = from;
    }

    public static FromSizeRequest of(int from, int size) {
        return of(from, size, Sort.unsorted());
    }

    public static FromSizeRequest of(int from, int size, Sort sort) {
        return new FromSizeRequest(from, size, sort);
    }

}