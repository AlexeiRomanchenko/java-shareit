package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShortItemBookingDto {
    private static final String FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private Long id;
    private Long bookerId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FORMAT)
    private LocalDateTime start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FORMAT)
    private LocalDateTime end;

}