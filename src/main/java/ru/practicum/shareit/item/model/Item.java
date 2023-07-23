package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@Builder
public class Item {
    @Positive
    private Long id;
    @NotBlank
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private ItemRequest request;
}