package ru.practicum.shareit.booking.description;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.exception.BadRequestException;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum BookingTimeStatus {
    ALL("ALL"),
    CURRENT("CURRENT"),
    PAST("PAST"),
    FUTURE("FUTURE"),
    WAITING("WAITING"),
    REJECTED("REJECTED");
    private final String message;

    public static BookingTimeStatus getStatusByValue(String status) {
        return Arrays.stream(values())
                .filter(value -> value.getMessage().equals(status))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(String.format("Unknown state: %s", status)));
    }

}