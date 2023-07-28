package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequest {
    @Positive
    private Long id;
    private String description;
    @NotNull
    private String requester;
    @NotNull
    @FutureOrPresent
    private LocalDateTime created;

}