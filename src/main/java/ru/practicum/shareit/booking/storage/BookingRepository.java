package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.description.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Modifying
    @Query("UPDATE Booking b "
            + "SET b.status = :status  "
            + "WHERE b.id = :bookingId")
    void save(BookingStatus status, Long bookingId);

    List<Booking> findByBookerId(Long id, Pageable page);

    List<Booking> findByBookerIdAndStatusIs(Long id, BookingStatus status, Pageable page);

    List<Booking> findByBookerIdAndEndIsAfterAndStartIsBefore(Long id,
                                                                              LocalDateTime end,
                                                                              LocalDateTime start,
                                                                              Pageable page);

    List<Booking> findByBookerIdAndEndIsBefore(Long id, LocalDateTime time, Pageable page);

    List<Booking> findByBookerIdAndStartIsAfter(Long id, LocalDateTime time, Pageable page);

    List<Booking> findByBookerIdAndStartIsAfterAndStatusIs(Long bookerId,
                                                                           LocalDateTime start,
                                                                           BookingStatus status,
                                                                           Pageable page);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.ownerId = :ownerId ")
    List<Booking> findByItemOwnerId(Long ownerId, Pageable page);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.ownerId = :ownerId "
            + "AND :time between b.start AND b.end ")
    List<Booking> findCurrentBookingsOwner(Long ownerId, LocalDateTime time, Pageable page);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.ownerId = :ownerId "
            + "AND b.end < :time ")
    List<Booking> findPastBookingsOwner(Long ownerId, LocalDateTime time, Pageable page);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.ownerId = :ownerId "
            + "AND b.start > :time ")
    List<Booking> findFutureBookingsOwner(Long ownerId, LocalDateTime time, Pageable page);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.ownerId = :ownerId "
            + "AND b.start > :time AND b.status = :status ")
    List<Booking> findWaitingBookingsOwner(Long ownerId, LocalDateTime time, BookingStatus status, Pageable page);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.ownerId = :ownerId "
            + "AND b.status = :status ")
    List<Booking> findRejectedBookingsOwner(Long ownerId, BookingStatus status, Pageable page);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.id = :itemId ")
    List<Booking> findBookingsItem(Long itemId);

    List<Booking> findByItemIdAndBookerIdAndStatusIsAndEndIsBefore(Long itemId,
                                                                   Long bookerId,
                                                                   BookingStatus status,
                                                                   LocalDateTime time);

    List<Booking> findAllByItemIdIn(List<Long> itemIds);

}