package ru.practicum.shareit.booking.description;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class Page extends PageRequest {
    protected Page(int page, int size, Sort sort) {
        super(page, size, sort);
    }

}
