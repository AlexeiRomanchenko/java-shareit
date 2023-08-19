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

}