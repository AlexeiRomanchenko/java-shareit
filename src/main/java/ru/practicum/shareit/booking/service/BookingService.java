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
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.storage.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;


    @Transactional
    public OutputBookingDto create(InputBookingDto bookingDtoShort, Long bookerId) {
        if (bookingDtoShort.getEnd().isBefore(bookingDtoShort.getStart()) ||
                bookingDtoShort.getEnd().equals(bookingDtoShort.getStart())) {
            throw new TimeDataException(String
                    .format("Время начала = %s  и конца = %s бронирования - неверное",
                            bookingDtoShort.getStart(), bookingDtoShort.getEnd()));
        }
        User booker = UserMapper.toUser(checkFindUserById(bookerId));
        Item item = ItemMapper.toItem(findItemById(bookingDtoShort.getItemId(), bookerId));
        if (findOwnerId(item.getId()).equals(bookerId)) {
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
        checkFindUserById(userId);
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
        checkFindUserById(ownerId);
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
        Long ownerId = findOwnerId(booking.getItem().getId());
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

    public UserDto checkFindUserById(Long id) {
        return UserMapper.toUserDto(userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Пользователь с id " + id + " не найден")));
    }

    public ItemDto findItemById(Long itemId, Long userId) {
        ItemDto result;
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с ID = %d не найдена.", itemId)));
        result = ItemMapper.toItemDto(item);
        if (Objects.equals(item.getOwnerId(), userId)) {
            updateBookings(result);
        }
        List<Comment> comments = commentRepository.findAllByItemId(result.getId());
        result.setComments(CommentMapper.toDtoList(comments));
        return result;
    }

    public ItemDto updateBookings(ItemDto itemDto) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findBookingsItem(itemDto.getId());
        Booking lastBooking = bookings.stream()
                .filter(obj -> !(obj.getStatus().equals(BookingStatus.REJECTED)))
                .filter(obj -> obj.getStart().isBefore(now))
                .min((obj1, obj2) -> obj2.getStart().compareTo(obj1.getStart())).orElse(null);
        Booking nextBooking = bookings.stream()
                .filter(obj -> !(obj.getStatus().equals(BookingStatus.REJECTED)))
                .filter(obj -> obj.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart)).orElse(null);
        if (lastBooking != null) {
            itemDto.setLastBooking(BookingMapper.toItemBookingDto(lastBooking));
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(BookingMapper.toItemBookingDto(nextBooking));
        }
        return itemDto;
    }

    public Long findOwnerId(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с ID = %d не найдена.", itemId)))
                .getOwnerId();
    }

}