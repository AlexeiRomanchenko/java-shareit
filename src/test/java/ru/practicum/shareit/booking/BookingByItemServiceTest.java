package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.description.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingByItemService;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.storage.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class BookingByItemServiceTest {
    @InjectMocks
    private BookingByItemService bookingByItemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;

    private final User user = new User(1L, "user@email.com", "User");
    private final UserDto userDto = new UserDto(1L, "user@email.com", "User");
    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .description("description")
            .requester(user)
            .items(new ArrayList<>())
            .build();
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("description")
            .requester(userDto)
            .items(new ArrayList<>())
            .build();
    private final Item item = Item.builder()
            .id(1L)
            .name("ItemName")
            .description("description")
            .available(true)
            .itemRequest(itemRequest)
            .ownerId(1L)
            .build();

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("ItemName")
            .description("description")
            .available(true)
            .requestId(1L)
            .build();

    private final List<Booking> bookingList = List.of(Booking.builder()
                    .id(1L).item(item).booker(user)
                    .start(LocalDateTime.now().minusHours(2L))
                    .end(LocalDateTime.now().minusHours(1L))
                    .status(BookingStatus.WAITING).build(),
            Booking.builder()
                    .id(2L).item(item).booker(user)
                    .start(LocalDateTime.now().plusHours(1L))
                    .end(LocalDateTime.now().plusHours(2L))
                    .status(BookingStatus.WAITING).build());

    private final Comment comment = Comment.builder().id(1L).text("Text").item(item).author(user).build();
    private final CommentDto commentDto =
            CommentDto.builder().id(1L).text("Text").item(itemDto).authorName("User").build();

    @Test
    void findByIdWhenItemNotFoundThenReturnedNotFoundException() {
        Mockito.when(itemRepository.findById(anyLong()))
                .thenThrow(new NotFoundException(String.format("Вещь с ID = %d не найдена.", 100L)));

        Exception e = assertThrows(NotFoundException.class, () -> bookingByItemService
                .findItemById(100L, 1L));

        assertEquals(e.getMessage(), String.format("Вещь с ID = %d не найдена.", 100L));
    }

    @Test
    void findByIdWhenParamsIsValidThenReturnedExpectedItem() {
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Mockito.when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(List.of(comment));

        Mockito.when(bookingRepository.findBookingsItem(anyLong()))
                .thenReturn(bookingList);

        ItemDto requestedItemDto = bookingByItemService.findItemById(1L, 1L);

        assertEquals(requestedItemDto.getName(), item.getName());
        assertEquals(requestedItemDto.getDescription(), item.getDescription());
        assertEquals(requestedItemDto.getAvailable(), item.getAvailable());
        assertEquals(requestedItemDto.getLastBooking().getId(), 1L);
        assertEquals(requestedItemDto.getLastBooking().getBookerId(), 1L);
        assertEquals(requestedItemDto.getNextBooking().getId(), 2L);
        assertEquals(requestedItemDto.getNextBooking().getBookerId(), 1L);
    }

}
