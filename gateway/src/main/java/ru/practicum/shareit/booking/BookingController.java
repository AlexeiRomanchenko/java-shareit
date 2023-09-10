package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(OWNER_ID_HEADER) long userId,
                                         @Valid @RequestBody BookingDto bookingDtoShort) {
        log.info("Получен запрос на добавление бронирования пользователем с ID = {}", userId);
        return bookingClient.addBooking(userId, bookingDtoShort);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                           @PathVariable Long bookingId) {
        log.info("Получен запрос на получение бронирования с ID = {}", bookingId);
        return bookingClient.getBookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findByUserId(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                               @RequestParam(defaultValue = "ALL") String state,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос на получение всех бронирований пользователя с Id = {}", userId);
        return bookingClient.getBookingsByUserId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findByOwnerId(@RequestHeader(OWNER_ID_HEADER) Long ownerId,
                                                @RequestParam(defaultValue = "ALL") String state,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос на получение всех бронирований владельца с ID = {}", ownerId);
        return bookingClient.getBookingsByOwnerId(ownerId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> save(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                       @PathVariable Long bookingId,
                                       @RequestParam Boolean approved) {
        log.info("Получен запрос на обновление бронирования с ID = {}", bookingId);
        return bookingClient.approveBooking(bookingId, userId, approved);
    }

}