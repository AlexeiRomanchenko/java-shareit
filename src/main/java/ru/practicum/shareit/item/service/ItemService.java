package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.booking.description.BookingStatus;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.dto.ShortItemBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.FromSizeRequest;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OperationAccessException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.storage.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestService requestService;

    public ItemDto create(Long userId, ItemDto itemDto) {
        checkFindUserById(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(userId);
        item.setItemRequest(itemDto.getRequestId() != null ?
                ItemRequestMapper.toItemRequest(requestService.findById(userId, itemDto.getRequestId())) : null);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public List<ItemDto> findUserItems(Long userId, Integer from, Integer size) {
        Pageable page = FromSizeRequest.of(from, size);

        List<ItemDto> items = itemRepository.findAllByOwnerId(userId, page).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        List<Long> itemIds = items.stream().map(ItemDto::getId).collect(Collectors.toList());

        List<CommentDto> comments = commentRepository.findAllByItemIdIn(itemIds).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());

        Map<Long, List<CommentDto>> commentsByItemId = comments.stream()
                .collect(Collectors.groupingBy(CommentDto::getId));

        List<OutputBookingDto> bookings = bookingRepository.findAllByItemIdIn(itemIds).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());

        Map<Long, List<OutputBookingDto>> bookingsByItemId = bookings.stream()
                .collect(Collectors.groupingBy(bookingDto -> bookingDto.getItem().getId()));

        items.forEach(i -> {
            i.setComments(commentsByItemId.getOrDefault(i.getId(), new ArrayList<>()));
            i.setLastBooking(searchLastBookingByItemId(bookingsByItemId.getOrDefault(i.getId(), new ArrayList<>())));
            i.setNextBooking(searchNextBookingByItemId(bookingsByItemId.getOrDefault(i.getId(), new ArrayList<>())));
        });
        return items;
    }

    @Transactional(readOnly = true)
    public ShortItemBookingDto searchLastBookingByItemId(List<OutputBookingDto> bookings) {
        LocalDateTime now = LocalDateTime.now();
        OutputBookingDto lastBooking = bookings.stream()
                .filter(obj -> !(obj.getStatus().equals(BookingStatus.REJECTED)))
                .filter(obj -> obj.getStart().isBefore(now))
                .min((obj1, obj2) -> obj2.getStart().compareTo(obj1.getStart())).orElse(null);
        if (lastBooking != null) {
            return BookingMapper.toItemBookingDto(lastBooking);
        }
        return null;
    }

    @Transactional(readOnly = true)
    public ShortItemBookingDto searchNextBookingByItemId(List<OutputBookingDto> bookings) {
        LocalDateTime now = LocalDateTime.now();
        OutputBookingDto nextBooking = bookings.stream()
                .filter(obj -> !(obj.getStatus().equals(BookingStatus.REJECTED)))
                .filter(obj -> obj.getStart().isAfter(now))
                .min(Comparator.comparing(OutputBookingDto::getStart)).orElse(null);

        if (nextBooking != null) {
            return BookingMapper.toItemBookingDto(nextBooking);
        }
        return null;
    }

    public ItemDto save(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с ID = %d не найдена.", itemId)));
        checkFindUserById(userId);
        if (!item.getOwnerId().equals(userId)) {
            throw new OperationAccessException(String.format(
                    "Пользователь с ID = %d не является владельцем, обновление не доступно.", userId));
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
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

    public void deleteById(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Transactional(readOnly = true)
    public List<ItemDto> search(String text, Integer from, Integer size) {
        Pageable page = FromSizeRequest.of(from, size);
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchAvailableItems(text, page).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long findOwnerId(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с ID = %d не найдена.", itemId)))
                .getOwnerId();
    }

    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с ID = %d не найдена.", itemId)));
        User user = UserMapper.toUser(checkFindUserById(userId));
        List<Booking> bookings = bookingRepository
                .findByItemIdAndBookerIdAndStatusIsAndEndIsBefore(
                        itemId, userId, BookingStatus.APPROVED, LocalDateTime.now());
        log.info(bookings.toString());
        if (!bookings.isEmpty() && bookings.get(0).getStart().isBefore(LocalDateTime.now())) {
            Comment comment = CommentMapper.toComment(commentDto);
            comment.setItem(item);
            comment.setAuthor(user);
            comment.setCreated(LocalDateTime.now());
            return CommentMapper.toDto(commentRepository.save(comment));
        } else {
            throw new NotAvailableException(String.format(
                    "Бронирование не найдено для пользователя с ID = %d и вещи с ID = %d.", userId, itemId));
        }
    }

    @Transactional(readOnly = true)
    public UserDto checkFindUserById(Long id) {
        return UserMapper.toUserDto(userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Пользователь с id " + id + " не найден")));
    }

}