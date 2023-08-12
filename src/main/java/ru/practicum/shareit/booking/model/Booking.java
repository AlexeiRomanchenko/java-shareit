package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.booking.description.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;
    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id")
    private User booker;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Booking)) return false;
        final Booking other = (Booking) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$start = this.getStart();
        final Object other$start = other.getStart();
        if (this$start == null ? other$start != null : !this$start.equals(other$start)) return false;
        final Object this$end = this.getEnd();
        final Object other$end = other.getEnd();
        if (this$end == null ? other$end != null : !this$end.equals(other$end)) return false;
        final Object this$item = this.getItem();
        final Object other$item = other.getItem();
        if (this$item == null ? other$item != null : !this$item.equals(other$item)) return false;
        final Object this$booker = this.getBooker();
        final Object other$booker = other.getBooker();
        if (this$booker == null ? other$booker != null : !this$booker.equals(other$booker)) return false;
        final Object this$status = this.getStatus();
        final Object other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Booking;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $start = this.getStart();
        result = result * PRIME + ($start == null ? 43 : $start.hashCode());
        final Object $end = this.getEnd();
        result = result * PRIME + ($end == null ? 43 : $end.hashCode());
        final Object $item = this.getItem();
        result = result * PRIME + ($item == null ? 43 : $item.hashCode());
        final Object $booker = this.getBooker();
        result = result * PRIME + ($booker == null ? 43 : $booker.hashCode());
        final Object $status = this.getStatus();
        result = result * PRIME + ($status == null ? 43 : $status.hashCode());
        return result;
    }

}