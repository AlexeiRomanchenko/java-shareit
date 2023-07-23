package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

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
    private User requestor;
    @NotNull
    @FutureOrPresent
    private LocalDateTime created;
}