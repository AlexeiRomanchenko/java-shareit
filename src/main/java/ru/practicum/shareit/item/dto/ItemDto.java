package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@Builder
public class ItemDto {
    @Positive
    private Long id;
    @Size(max = 255)
    @NotBlank
    private String name;
    @Size(max = 500)
    @NotBlank
    private String description;
    @NotBlank
    private Boolean isAvailable;
    private Long requestId;
}