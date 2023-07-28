package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
public class Booking {
    @Positive
    private Long id;
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull
    @FutureOrPresent
    private LocalDateTime end;
    @NotNull
    private Long itemId;
    @NotNull
    private Long bookerId;
    private String status;

}