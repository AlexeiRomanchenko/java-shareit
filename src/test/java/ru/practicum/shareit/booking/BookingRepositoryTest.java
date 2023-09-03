package ru.practicum.shareit.booking;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.description.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.FromSizeRequest;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    private final User user = new User(1L, "user1", "email1@ya.com");
    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .description("description")
            .requester(user)
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

    @Test
    public void testFindAllBookingsByBookerIdOrderByStartDesc() {
        Long bookerId = 1L;

        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookings = bookingRepository.findByBookerId(bookerId, pageable);

        assertNotNull(bookings);
        assertTrue(bookings.isEmpty());
    }

    @Test
    public void testFindAllBookingsByItemOwnerIdOrderByStartDesc() {
        Long ownerId = 1L;

        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookings = bookingRepository.findByItemOwnerId(ownerId, pageable);

        assertNotNull(bookings);
        assertTrue(bookings.isEmpty());
    }

    @Test
    void testItemSaved() throws Exception {
        LocalDateTime time = LocalDateTime.now();

        User userInRep = userRepository.save(user);
        Item itemInRep = itemRepository.save(item);

        Booking booking = new Booking(1L,time.plusMinutes(1), time.plusDays(1), itemInRep,
                userInRep, BookingStatus.WAITING);

        Booking bookingInRep = bookingRepository.save(booking);

        Booking retrievedBooking = entityManager.find(Booking.class, bookingInRep.getId());

        assertThat(retrievedBooking).isEqualTo(bookingInRep);
    }

    @Test
    public void testFindAllByOwnerIdAndItemIdAndEndBeforeOrderByStartDesc() {
        User owner = new User(1L, "user@email.com", "User");
        User booker = new User(1L, "user@email.com", "User");

        owner = userRepository.save(owner);
        booker = userRepository.save(booker);

        Item item = Item.builder()
                .available(true)
                .description("ttt")
                .ownerId(owner.getId())
                .name("name").build();

        item = itemRepository.save(item);

        Booking booking1 = Booking.builder()
                .booker(booker)
                .id(1L)
                //.status(BookingStatus.WAITING)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now())
                .item(item).build();

        Booking booking2 = Booking.builder()
                .booker(booker)
                .id(1L)
                //.status(BookingStatus.WAITING)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(2))
                .item(item).build();


        /*Booking booking3 = Booking.builder()
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1)).build();
*/

       bookingRepository.save(booking1);
       bookingRepository.save(booking2);
        //booking3 = bookingRepository.save(booking3);
        PageRequest page = FromSizeRequest.of(0, 5, Sort.by("start").descending());

        assertThat(bookingRepository.findByBookerIdAndEndIsBefore(booking1.getId(), LocalDateTime.now(), page
                 )).isEqualTo(List.of(booking1, booking2));
    }

}