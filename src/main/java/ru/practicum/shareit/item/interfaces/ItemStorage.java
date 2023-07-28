package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item create(Item item);

    Item update(Item item);

    Item delete(Long userId);

    List<Item> getItemsByOwner(Long ownerId);

    List<Item> getItemsBySearchQuery(String text);

    Item getItemById(Long itemId);

    void deleteItemsByOwner(Long ownerId);

}