package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public OutputBookingDto create(@RequestHeader(OWNER_ID_HEADER) long userId,
                                   @RequestBody InputBookingDto bookingDtoShort) {
        log.info("Получен запрос на добавление бронирования пользователем с ID = {}", userId);
        return bookingService.create(bookingDtoShort, userId);
    }

    @GetMapping("/{bookingId}")
    public OutputBookingDto findById(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                     @PathVariable Long bookingId) {
        log.info("Получен запрос на получение бронирования с ID = {}", bookingId);
        return bookingService.findBookingById(bookingId, userId);
    }

    @GetMapping
    public List<OutputBookingDto> findByUserId(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                               @RequestParam(defaultValue = "ALL") String state,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос на получение всех бронирований пользователя с Id = {}", userId);
        return bookingService.findAllBookingsByUser(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<OutputBookingDto> findByOwnerId(@RequestHeader(OWNER_ID_HEADER) Long ownerId,
                                                @RequestParam(defaultValue = "ALL") String state,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос на получение всех бронирований владельца с ID = {}", ownerId);
        return bookingService.findAllBookingsByOwner(state, ownerId, from, size);
    }

    @PatchMapping("/{bookingId}")
    public OutputBookingDto save(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                 @PathVariable Long bookingId,
                                 @RequestParam Boolean approved) {
        log.info("Получен запрос на обновление бронирования с ID = {}", bookingId);
        return bookingService.approve(bookingId, userId, approved);
    }

}