package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.interfaces.ItemStorage;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Repository
public class ItemStorageImpl implements ItemStorage {

    private final Map<Long, Item> items;


    private Long currentId;

    public ItemStorageImpl() {
        currentId = 0L;
        items = new HashMap<>();
    }

    @Override
    public Item create(Item item) {
        item.setId(++currentId);
        items.put(item.getId(), item);
        return item;
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
    public List<Item> getItemsByOwner(Long ownerId) {
        return new ArrayList<>(items
                .values()
                .stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .collect(toList()));
    }

    @Override
    public List<Item> getItemsBySearchQuery(String text) {
        return items.values()
                .stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text) ||
                        item.getDescription().toLowerCase().contains(text))
                .collect(toList());
    }

    @Override
    public Item getItemById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public void deleteItemsByOwner(Long ownerId) {
        items.values()
                .stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .map(Item::getId)
                .forEach(id -> items.remove(id));
    }

}