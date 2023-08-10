package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.booking.description.BookingStatus;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OperationAccessException;
import ru.practicum.shareit.exception.TimeDataException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Transactional
    public OutputBookingDto create(InputBookingDto bookingDtoShort, Long bookerId) {
        if (bookingDtoShort.getEnd().isBefore(bookingDtoShort.getStart()) ||
                bookingDtoShort.getEnd().equals(bookingDtoShort.getStart())) {
            throw new TimeDataException(String
                    .format("Время начала = %s  и конца = %s бронирования - неверное",
                            bookingDtoShort.getStart(), bookingDtoShort.getEnd()));
        }
        User booker = UserMapper.toUser(userService.checkFindUserById(bookerId));
        Item item = ItemMapper.toItem(itemService.findItemById(bookingDtoShort.getItemId(), bookerId));
        if (itemService.findOwnerId(item.getId()).equals(bookerId)) {
            throw new OperationAccessException("Владелец не может бронировать свою вещь.");
        }
        if (item.getAvailable()) {
            Booking booking = Booking.builder()
                    .start(bookingDtoShort.getStart())
                    .end(bookingDtoShort.getEnd())
                    .item(item)
                    .booker(booker)
                    .status(BookingStatus.WAITING)
                    .build();
            return BookingMapper.toBookingDto(bookingRepository.save(booking));
        } else {
            throw new NotAvailableException(String.format("Вещь с ID = %d не доступна.", item.getId()));
        }
    }

    @Transactional
    public OutputBookingDto findBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Бронирование с ID = %d не найдено.", bookingId)));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwnerId().equals(userId)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new OperationAccessException(String.format(
                    "Пользователь с ID = %d не является владельцем, доступ закрыт.", userId));
        }
    }

    @Transactional
    public List<OutputBookingDto> findAllBookingsByUser(String state, Long userId, Integer from, Integer size) {
        userService.checkFindUserById(userId);
        Pageable page = PageRequest.of(from / size, size);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                return BookingMapper.toBookingDto(bookingRepository.findByBookerIdOrderByStartDesc(userId, page));
            case "CURRENT":
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(userId, now, now, page));
            case "PAST":
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndEndIsBeforeOrderByStartDesc(userId, now, page));
            case "FUTURE":
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndStartIsAfterOrderByStartDesc(userId, now, page));
            case "WAITING":
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndStartIsAfterAndStatusIsOrderByStartDesc(userId, now,
                                BookingStatus.WAITING, page));
            case "REJECTED":
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.REJECTED, page));

        }
        throw new BadRequestException(String.format("Unknown state: %s", state));
    }

    @Transactional
    public List<OutputBookingDto> findAllBookingsByOwner(String state, Long ownerId, Integer from, Integer size) {
        userService.checkFindUserById(ownerId);
        Pageable page = PageRequest.of(from / size, size);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                return BookingMapper.toBookingDto(bookingRepository.findByItemOwnerId(ownerId, page));
            case "CURRENT":
                return BookingMapper.toBookingDto(bookingRepository.findCurrentBookingsOwner(ownerId, now, page));
            case "PAST":
                return BookingMapper.toBookingDto(bookingRepository.findPastBookingsOwner(ownerId, now, page));
            case "FUTURE":
                return BookingMapper.toBookingDto(bookingRepository.findFutureBookingsOwner(ownerId, now, page));
            case "WAITING":
                return BookingMapper.toBookingDto(bookingRepository
                        .findWaitingBookingsOwner(ownerId, now, BookingStatus.WAITING, page));
            case "REJECTED":
                return BookingMapper.toBookingDto(bookingRepository
                        .findRejectedBookingsOwner(ownerId, BookingStatus.REJECTED, page));
        }
        throw new BadRequestException(String.format("Unknown state: %s", state));
    }

    @Transactional
    public OutputBookingDto approve(long bookingId, long userId, Boolean approve) {
        OutputBookingDto booking = findBookingById(bookingId, userId);
        Long ownerId = itemService.findOwnerId(booking.getItem().getId());
        if (ownerId.equals(userId)
                && booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new AlreadyExistsException("Бронирование уже подтверждено.");
        }
        if (!ownerId.equals(userId)) {
            throw new OperationAccessException(String.format(
                    "Пользователь с ID = %d не является владельцем. Доступ закрыт.", userId));
        }
        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
            bookingRepository.save(BookingStatus.APPROVED, bookingId);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            bookingRepository.save(BookingStatus.REJECTED, bookingId);
        }
        return booking;
    }

}