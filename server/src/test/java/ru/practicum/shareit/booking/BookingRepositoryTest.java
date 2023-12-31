package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

}