package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.description.BookingStatus;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingByItemService;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @InjectMocks
    private BookingService bookingService;
    @Mock
    private ItemService itemService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingByItemService bookingByItemService;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;

    private final User user1 = new User(1L, "user@email.com", "User");
    private final InputBookingDto inputBookingDto = InputBookingDto.builder()
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(1L))
            .itemId(1L)
            .build();
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Item")
            .description("Description")
            .available(true)
            .requestId(1L)
            .build();
    private final Item item = Item.builder()
            .id(1L)
            .name("Item")
            .description("Description")
            .available(true)
            .ownerId(1L)
            .build();

    private final Item item2 = Item.builder()
            .id(1L)
            .name("Item2")
            .description("Description2")
            .available(true)
            .ownerId(2L)
            .build();

    private final Booking booking1 = Booking.builder()
            .booker(user1)
            .id(1L)
            .status(BookingStatus.APPROVED)
            .item(item).build();

    private final Booking booking2 = Booking.builder()
            .booker(user1)
            .id(1L)
            .status(BookingStatus.WAITING)
            .item(item).build();


    @Test
    void createBookingWhenTimeIsNotValidThenReturnedTimeDataException() {
        InputBookingDto bookingBadTime = InputBookingDto.builder()
                .start(LocalDateTime.now().plusHours(1L))
                .end(LocalDateTime.now().minusHours(1L))
                .itemId(1L)
                .build();

        Exception e = assertThrows(TimeDataException.class,
                () -> bookingService.create(bookingBadTime, 1L));
        assertEquals(e.getMessage(), String.format("Время начала = %s и конца = %s бронирования - неверное",
                bookingBadTime.getStart(), bookingBadTime.getEnd()));
    }

    @Test
    void createBookingWhenUserIsNotOwnerThenReturnedOperationAccessException() {
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));

        Mockito.when(itemService.findItemById(anyLong(), anyLong()))
                .thenReturn(itemDto);

        Mockito.when(bookingByItemService.findItemById(anyLong(), anyLong()))
                .thenReturn(itemDto);

        Mockito.when(bookingByItemService.findOwnerId(anyLong()))
                .thenReturn(1L);

        Mockito.when(itemService.findOwnerId(anyLong()))
                .thenReturn(1L);

        Exception e = assertThrows(OperationAccessException.class,
                () -> bookingService.create(inputBookingDto, 1L));

        assertEquals(e.getMessage(), "Владелец не может бронировать свою вещь.");
    }

    @Test
    void createBookingWhenItemIsNotAvailableThenReturnedNotAvailableException() {
        itemDto.setAvailable(false);

        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));

        Mockito.when(itemService.findItemById(anyLong(), anyLong()))
                .thenReturn(itemDto);

        Mockito.when(bookingByItemService.findItemById(anyLong(), anyLong()))
                .thenReturn(itemDto);

        Mockito.when(itemService.findOwnerId(anyLong()))
                .thenReturn(1L);

        Exception e = assertThrows(NotAvailableException.class,
                () -> bookingService.create(inputBookingDto, 2L));

        assertEquals(e.getMessage(), String.format("Вещь с ID = %d не доступна.", 1L));
    }

    @Test
    void createBookingWhenItemIsAvailableThenReturnedNotAvailableException() {
        itemDto.setAvailable(true);

    }

    @Test
    void findBookingByIdWhenBookingIsNotFoundThenReturnedNotFoundException() {
        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception e = assertThrows(NotFoundException.class,
                () -> bookingService.findBookingById(1L, 1L));

        assertEquals(e.getMessage(), String.format("Бронирование с ID = %d не найдено.", 1L));
    }

    @Test
    void findBookingByIdWhenUserIsNotOwnerThenReturnedOperationAccessException() {
        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking1));
        Exception e = assertThrows(OperationAccessException.class,
                () -> bookingService.findBookingById(1L, 100L));

        assertEquals(e.getMessage(), String.format("Пользователь с ID = %d не является владельцем, доступ закрыт.", 100L));
    }

    @Test
    void getAllBookingsByUserIdWhenStateIsUnknownThenReturnedBadRequestException() {
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));

        Exception e = assertThrows(BadRequestException.class,
                () -> bookingService.findAllBookingsByUser("bla", 1L, 0, 10));

        assertEquals(e.getMessage(), "Unknown state: bla");
    }

    @Test
    void getAllBookingsByOwnerIdWhenStateIsUnknownThenReturnedBadRequestException() {
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        Exception e = assertThrows(BadRequestException.class,
                () -> bookingService.findAllBookingsByOwner("bla", 1L, 0, 10));

        assertEquals(e.getMessage(), "Unknown state: bla");
    }


    @ParameterizedTest
    @CsvSource(value = {
            "ALL, 0, 2",
            "CURRENT, -1, 1",
            "PAST, -2, -1",
            "FUTURE, 1, 2",
            "WAITING, 0, 1"
    })
    void getByUserIdAndStateTest(String state, int addToStart, int addToEnd) {
        LocalDateTime start = LocalDateTime.now().plusDays(addToStart);
        LocalDateTime end = LocalDateTime.now().plusDays(addToEnd);
        User testUser1 = new User(1L, "user@email.com", "User");
        Item testItem = item2;
        Booking testBooking = booking1;
        testBooking.setBooker(testUser1);
        testBooking.setStart(start);
        testBooking.setEnd(end);

        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.findByBookerId(anyLong(), any()))
                .thenReturn(List.of(testBooking));
        Mockito.when(bookingRepository.findByBookerIdAndEndIsAfterAndStartIsBefore(
                anyLong(), any(), any(), any())).thenReturn(List.of(testBooking));
        Mockito.when(bookingRepository.findByBookerIdAndEndIsBefore(anyLong(),
                any(), any())).thenReturn(List.of(testBooking));
        Mockito.when(bookingRepository.findByBookerIdAndStartIsAfter(anyLong(),
                any(), any())).thenReturn(List.of(testBooking));
        Mockito.when(bookingRepository.findByBookerIdAndStartIsAfterAndStatusIs(anyLong(),
                any(), any(), any())).thenReturn(List.of(testBooking));
        List<OutputBookingDto> bookings = bookingService.findAllBookingsByUser(state, testUser1.getId(), 0, 10);
        assertFalse(bookings.isEmpty());
        assertEquals(bookings.get(0).getId(), 1L);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "ALL, 0, 2",
            "CURRENT, -1, 1",
            "PAST, -2, -1",
            "FUTURE, 1, 2",
    })
    void getByItemOwnerIdAndStateTest(String state, int addToStart, int addToEnd) {
        LocalDateTime start = LocalDateTime.now().plusDays(addToStart);
        LocalDateTime end = LocalDateTime.now().plusDays(addToEnd);
        User booker = new User(1L, "User", "user@email.com");
        User itemOwner = new User(2L, "User2", "user2@email.com");
        Item testItem = Item.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .ownerId(2L)
                .build();
        Booking testBooking = Booking.builder()
                .booker(user1)
                .id(1L)
                .status(BookingStatus.APPROVED)
                .item(item).build();
        testBooking.setBooker(booker);
        testBooking.setStart(start);
        testBooking.setEnd(end);

        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        Mockito.when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        Mockito.when(bookingRepository.findByItemOwnerId(anyLong(), any()))
                .thenReturn(List.of(testBooking));
        Mockito.when(bookingRepository.findCurrentBookingsOwner(
                anyLong(), any(), any())).thenReturn(List.of(testBooking));
        Mockito.when(bookingRepository.findByBookerIdAndEndIsBefore(anyLong(),
                any(), any())).thenReturn(List.of(testBooking));
        Mockito.when(bookingRepository.findPastBookingsOwner(
                anyLong(), any(), any())).thenReturn(List.of(testBooking));
        Mockito.when(bookingRepository.findFutureBookingsOwner(anyLong(),
                any(), any())).thenReturn(List.of(testBooking));
        List<OutputBookingDto> bookings = bookingService.findAllBookingsByOwner(state, itemOwner.getId(), 0, 10);
        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0).getId(), 1L);
    }

    @Test
    void approveWhenBookingDecisionThenReturnedAlreadyExistsException() {
        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking1));

        Mockito.when(itemService.findOwnerId(anyLong()))
                .thenReturn(1L);

        Exception e = assertThrows(AlreadyExistsException.class,
                () -> bookingService.approve(1L, 1L, true));

        assertEquals(e.getMessage(), "Бронирование уже подтверждено.");
    }

    @Test
    void approveWhenUserIsNotOwnerThenReturnedOperationAccessException() {
        Mockito.when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking2));

        Exception e = assertThrows(OperationAccessException.class,
                () -> bookingService.approve(1L, 1L, true));

        assertEquals(e.getMessage(), String.format("Пользователь с ID = %d не является владельцем. Доступ закрыт.", 1L));
    }

}