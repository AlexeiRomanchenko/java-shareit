package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    Collection<Item> getAll();

    Item create(Item item);

    Item update(Item item);

    Item delete(Long userId);

    Item getItemById(Long itemId);

}