package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
public class ItemRequest {
    private Long id;
    private String description;
    private String requester;
    private LocalDateTime created;

}