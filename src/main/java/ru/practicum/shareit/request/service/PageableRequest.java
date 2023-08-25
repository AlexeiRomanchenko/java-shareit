package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageableRequest extends PageRequest {

    private PageableRequest(int from, int size, Sort sort) {
        super(from / size, size, sort);
    }

}