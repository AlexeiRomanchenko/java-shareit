package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.description.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    private final User booker = new User(1L, "user2", "email1@ya.com");
    private final User owner = new User(2L, "user1", "email2@ya.com");
    private final Pageable page = PageRequest.of(0, 10, Sort.unsorted());

    private final Item item = Item.builder()
            .id(1L)
            .name("ItemName")
            .description("description")
            .available(true)
            .ownerId(owner.getId())
            .build();

    @Test
    public void testFindByBookerIdIsEmpty() {
        Long bookerId = 1L;

        List<Booking> bookings = bookingRepository.findByBookerId(bookerId, page);

        assertNotNull(bookings);
        assertTrue(bookings.isEmpty());
    }

    @Test
    public void testFindByItemOwnerIdIsEmpty() {
        Long ownerId = 1L;

        List<Booking> bookings = bookingRepository.findByItemOwnerId(ownerId, page);

        assertNotNull(bookings);
        assertTrue(bookings.isEmpty());
    }

    @Test
    public void testFindByBookerIdAndStartIsAfterAndStatusIs() {
        LocalDateTime now = LocalDateTime.now();

        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);

        Booking booking1 = Booking.builder()
                .id(1L)
                .booker(booker)
                .item(item)
                .start(now.minusMinutes(30))
                .status(BookingStatus.APPROVED)
                .end(now)
                .build();

        bookingRepository.save(booking1);

        Booking booking2 = Booking.builder()
                .id(2L)
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .start(now.plusMinutes(10))
                .end(now.plusMinutes(100))
                .build();

        bookingRepository.save(booking2);

        List<Booking> testBookings = bookingRepository.findByBookerIdAndStartIsAfterAndStatusIs(
                booker.getId(), now, BookingStatus.WAITING, page);

        int expectedSizeList = 1;

        assertNotNull(testBookings);
        assertFalse(testBookings.isEmpty());
        assertEquals(expectedSizeList, testBookings.size());

    }

}