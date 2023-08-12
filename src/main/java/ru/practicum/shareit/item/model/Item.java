package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "items")
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "is_available", nullable = false)
    private Boolean available;
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;
    @Transient
    private Booking lastBooking;
    @Transient
    private Booking nextBooking;
    @Transient
    private List<CommentDto> comments;
    @Transient
    @JoinColumn(name = "request_id")
    private ItemRequest requestId;

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Item)) return false;
        final Item other = (Item) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$description = this.getDescription();
        final Object other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description))
            return false;
        final Object this$available = this.getAvailable();
        final Object other$available = other.getAvailable();
        if (this$available == null ? other$available != null : !this$available.equals(other$available)) return false;
        final Object this$ownerId = this.getOwnerId();
        final Object other$ownerId = other.getOwnerId();
        if (this$ownerId == null ? other$ownerId != null : !this$ownerId.equals(other$ownerId)) return false;
        final Object this$lastBooking = this.getLastBooking();
        final Object other$lastBooking = other.getLastBooking();
        if (this$lastBooking == null ? other$lastBooking != null : !this$lastBooking.equals(other$lastBooking))
            return false;
        final Object this$nextBooking = this.getNextBooking();
        final Object other$nextBooking = other.getNextBooking();
        if (this$nextBooking == null ? other$nextBooking != null : !this$nextBooking.equals(other$nextBooking))
            return false;
        final Object this$comments = this.getComments();
        final Object other$comments = other.getComments();
        if (this$comments == null ? other$comments != null : !this$comments.equals(other$comments)) return false;
        final Object this$requestId = this.getRequestId();
        final Object other$requestId = other.getRequestId();
        if (this$requestId == null ? other$requestId != null : !this$requestId.equals(other$requestId)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Item;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $description = this.getDescription();
        result = result * PRIME + ($description == null ? 43 : $description.hashCode());
        final Object $available = this.getAvailable();
        result = result * PRIME + ($available == null ? 43 : $available.hashCode());
        final Object $ownerId = this.getOwnerId();
        result = result * PRIME + ($ownerId == null ? 43 : $ownerId.hashCode());
        final Object $lastBooking = this.getLastBooking();
        result = result * PRIME + ($lastBooking == null ? 43 : $lastBooking.hashCode());
        final Object $nextBooking = this.getNextBooking();
        result = result * PRIME + ($nextBooking == null ? 43 : $nextBooking.hashCode());
        final Object $comments = this.getComments();
        result = result * PRIME + ($comments == null ? 43 : $comments.hashCode());
        final Object $requestId = this.getRequestId();
        result = result * PRIME + ($requestId == null ? 43 : $requestId.hashCode());
        return result;
    }

}