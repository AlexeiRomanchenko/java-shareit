package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class Item {
    @Positive(message = "Id должно быть положительным числом")
    private Long id;
    @Size(max = 255)
    @NotBlank (message = "Поле <<Name>> не должно быть пустым")
    private String name;
    @Size(max = 500)
    @NotBlank (message = "Поле <Description> не должно быть пустым")
    private String description;
    @NotBlank (message = "Поле <isAvailable> не должно быть пустым")
    private Boolean isAvailable;
    private Long ownerId;
    private Long requestId;
}