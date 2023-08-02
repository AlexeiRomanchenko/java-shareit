package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.interfaces.ItemStorage;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class ItemStorageImpl implements ItemStorage {

    private final Map<Long, Item> items;
    private final Map<Long, List<Long>> ownerItems;


    private Long currentId;

    public ItemStorageImpl() {
        currentId = 0L;
        items = new HashMap<>();
        ownerItems = new HashMap<>();
    }

    @Override
    public Item create(Item item) {
        item.setId(++currentId);
        items.put(item.getId(), item);
        return item;
    }

    public void addItemsToOwnerId(Long ownerId, List<Long> itemsId) {
        ownerItems.put(ownerId, itemsId);
    }

    public List<Long> getItemsByOwnerId(Long ownerId) {
        return ownerItems.get(ownerId);
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Collection<Item> getAll() {
        return items.values();
    }

    @Override
    public Item delete(Long itemId) {
        return items.remove(itemId);
    }

    @Override
    public Item getItemById(Long itemId) {
        return items.get(itemId);
    }

}