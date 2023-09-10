package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.description.BookingStatus;
import ru.practicum.shareit.booking.description.BookingTimeStatus;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final BookingByItemService bookingByItemService;

    public OutputBookingDto create(InputBookingDto bookingDtoShort, Long bookerId) {
        if (bookingDtoShort.getEnd().isBefore(bookingDtoShort.getStart()) ||
                bookingDtoShort.getEnd().equals(bookingDtoShort.getStart())) {
            throw new TimeDataException(String
                    .format("Время начала = %s и конца = %s бронирования - неверное",
                            bookingDtoShort.getStart(), bookingDtoShort.getEnd()));
        }
        User booker = UserMapper.toUser(checkFindUserById(bookerId));
        Item item = ItemMapper.toItem(bookingByItemService.findItemById(bookingDtoShort.getItemId(), bookerId));
        if (bookingByItemService.findOwnerId(item.getId()).equals(bookerId)) {
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

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public List<OutputBookingDto> findAllBookingsByUser(String state, Long userId, Integer from, Integer size) {
        checkFindUserById(userId);
        LocalDateTime now = LocalDateTime.now();
        PageRequest page = FromSizeRequest.of(from, size, Sort.by("start").ascending());
        BookingTimeStatus bookingTimeStatus = BookingTimeStatus.getStatusByValue(state);

        switch (bookingTimeStatus) {
            case ALL:
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerId(userId, page));
            case CURRENT:
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndEndIsAfterAndStartIsBefore(userId, now, now, page));
            case PAST:
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndEndIsBefore(userId, now, page));
            case FUTURE:
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndStartIsAfter(userId, now, page));
            case WAITING:
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndStartIsAfterAndStatusIs(userId, now,
                                BookingStatus.WAITING, page));
            case REJECTED:
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndStatusIs(userId,
                                BookingStatus.REJECTED, page));

        }
        throw new BadRequestException(String.format("Unknown state: %s", state));
    }

    @Transactional(readOnly = true)
    public List<OutputBookingDto> findAllBookingsByOwner(String state, Long ownerId, Integer from, Integer size) {
        checkFindUserById(ownerId);
        PageRequest page = FromSizeRequest.of(from, size, Sort.by("start").descending());
        LocalDateTime now = LocalDateTime.now();
        BookingTimeStatus bookingTimeStatus = BookingTimeStatus.getStatusByValue(state);
        switch (bookingTimeStatus) {
            case ALL:
                return BookingMapper.toBookingDto(bookingRepository.findByItemOwnerId(ownerId, page));
            case CURRENT:
                return BookingMapper.toBookingDto(bookingRepository.findCurrentBookingsOwner(ownerId, now, page));
            case PAST:
                return BookingMapper.toBookingDto(bookingRepository.findPastBookingsOwner(ownerId, now, page));
            case FUTURE:
                return BookingMapper.toBookingDto(bookingRepository.findFutureBookingsOwner(ownerId, now, page));
            case WAITING:
                return BookingMapper.toBookingDto(bookingRepository
                        .findWaitingBookingsOwner(ownerId, now, BookingStatus.WAITING, page));
            case REJECTED:
                return BookingMapper.toBookingDto(bookingRepository
                        .findRejectedBookingsOwner(ownerId, BookingStatus.REJECTED, page));
        }
        throw new BadRequestException(String.format("Unknown state: %s", state));
    }

    public OutputBookingDto approve(long bookingId, long userId, Boolean approve) {
        OutputBookingDto booking = findBookingById(bookingId, userId);
        Long ownerId = bookingByItemService.findOwnerId(booking.getItem().getId());
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
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

    @Transactional(readOnly = true)
    public UserDto checkFindUserById(Long id) {
        return UserMapper.toUserDto(userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Пользователь с id " + id + " не найден")));
    }

}