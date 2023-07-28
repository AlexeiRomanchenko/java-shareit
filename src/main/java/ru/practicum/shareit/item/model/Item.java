package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Builder(toBuilder = true)
@Data
@AllArgsConstructor
public class Item {
    @Positive
    private Long id;
    @NotBlank
    private String name;
    private String description;
    private Boolean available;
    @Positive
    private Long ownerId;
    @Positive
    private Long requestId;

}