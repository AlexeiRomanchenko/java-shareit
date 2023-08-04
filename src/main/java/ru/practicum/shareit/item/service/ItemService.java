package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.interfaces.ItemStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.interfaces.UserStorage;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemDto create(ItemDto itemDto, Long ownerId) {
        ItemDto newItemDto = null;
        checkUserOnNull(ownerId);
        UserDto owner = UserMapper.toUserDto(userStorage.getUserById(ownerId));

        if (owner != null) {
            Item item = ItemMapper.toItem(itemDto, ownerId);
            newItemDto = ItemMapper.toItemDto(itemStorage.create(item));
            setItemIdToOwnerItems(ownerId, newItemDto.getId());
        }
        return newItemDto;
    }

    public void setItemIdToOwnerItems(Long ownerId, Long itemId) {
        List<Long> itemsId = itemStorage.getItemsByOwnerId(ownerId);

        if (itemsId == null) {
            itemsId = new ArrayList<>();
        }

        itemsId.add(itemId);
        itemStorage.addItemsToOwnerId(ownerId, itemsId);
    }


    public ItemDto update(ItemDto itemDto, Long ownerId, Long itemId) {
        if (itemDto.getName() == null) {
            itemDto.setName(itemStorage.getItemById(itemId).getName());
        }
        if (itemDto.getDescription() == null) {
            itemDto.setDescription(itemStorage.getItemById(itemId).getDescription());
        }
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(itemStorage.getItemById(itemId).getAvailable());
        }
        checkUserOnNull(ownerId);

        if (itemDto.getId() == null) {
            itemDto.setId(itemId);
        }
        Item oldItem = itemStorage.getItemById(itemId);
        if (!oldItem.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("У пользователя нет такой вещи.");
        }
        Item item = ItemMapper.toItem(itemDto, ownerId);

        return ItemMapper.toItemDto(itemStorage.update(item));
    }

    public void checkUserOnNull(Long id) {
        Optional.ofNullable(userStorage.getUserById(id)).orElseThrow(
                () -> {
                    throw new NotFoundException("Пользователь с id " + id + " не найден");
                });
    }

    public ItemDto delete(Long itemId, Long ownerId) {
        Item item = itemStorage.getItemById(itemId);

        if (!item.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("У пользователя нет такой вещи.");
        }

        List<Long> items = itemStorage.getItemsByOwnerId(ownerId);
        items.remove(itemId);
        itemStorage.addItemsToOwnerId(ownerId, items);

        return ItemMapper.toItemDto(itemStorage.delete(itemId));
    }

    public List<ItemDto> getItemsByOwner(Long ownerId) {
        Collection<Long> itemsId = itemStorage.getItemsByOwnerId(ownerId);

        List<ItemDto> itemDtoList = itemsId.stream()
                .map(this::getItemById)
                .collect(toList());

        return itemDtoList;
    }

    public List<ItemDto> getItemsBySearchQuery(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        String finalText = text.toLowerCase();

        return itemStorage.getAll()
                .stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(finalText) ||
                        item.getDescription().toLowerCase().contains(finalText))
                .map(ItemMapper::toItemDto)
                .collect(toList());

    }

    public ItemDto getItemById(Long itemId) {
        return ItemMapper.toItemDto(itemStorage.getItemById(itemId));
    }

    public void deleteItemsByOwner(Long ownerId) {
        Collection<Long> itemsId = itemStorage.getItemsByOwnerId(ownerId);

        if (itemsId != null) {
            for (Long itemId : itemsId) {
                itemStorage.delete(itemId);
            }
            clearListItemsByOwnerId(ownerId);
        }
    }

    public void clearListItemsByOwnerId(Long ownerId) {
        itemStorage.addItemsToOwnerId(ownerId, new ArrayList<>());
    }

}