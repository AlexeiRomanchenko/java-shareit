package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemStorage {
    Collection<Item> getAll();

    Item create(Item item);

    List<Long> getItemsByOwnerId(Long ownerId);

    void addItemsToOwnerId(Long ownerId, List<Long> itemsId);

    Item update(Item item);

    Item delete(Long userId);

    Item getItemById(Long itemId);

}