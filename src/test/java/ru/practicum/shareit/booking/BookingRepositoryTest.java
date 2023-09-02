package ru.practicum.shareit.booking;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookingRepositoryTest {
    /*private BookingRepository bookingRepository;
    private final User user1 = new User(1L, "user@email.com", "User");
    private final Item item = Item.builder()
            .id(1L)
            .name("Item")
            .description("Description")
            .available(true)
            .ownerId(1L)
            .build();

    private final Booking booking1 = Booking.builder()
            .booker(user1)
            .id(1L)
            .status(BookingStatus.APPROVED)
            .item(item).build();

    /*@Test
    void createUserTest() {

        Mockito.when(bookingRepository.save(booking1.getStatus(),booking1.getId()))
                        .thenReturn(booking1);

        Booking savedBooking = bookingRepository.save(booking1);

        assertEquals(b, BookingStatus.WAITING);
        Mockito.verify(bookingRepository, Mockito.times(1)).save(booking1);
    }*/

  /*  @Test
    public void testSave() {
        bookingRepository.save(BookingStatus.WAITING, booking1.getId());

    assertEquals(booking1.getStatus(), BookingStatus.WAITING);
        Mockito.verify(bookingRepository, Mockito.times(1)).save(booking1);

    }*/

}