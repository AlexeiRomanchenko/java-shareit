package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InputBookingDto {
    private static final String FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    @NotNull
    private Long itemId;

    @NotNull
    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FORMAT)
    private LocalDateTime start;
    
    @NotNull
    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FORMAT)
    private LocalDateTime end;

}