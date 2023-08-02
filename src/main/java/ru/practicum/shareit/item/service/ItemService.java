package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.interfaces.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;
    private final ItemMapper mapper;
    private final UserService userService;

    public ItemDto create(ItemDto itemDto, Long ownerId) {
        ItemDto newItemDto = null;
        if (userService.getUserById(ownerId) != null) {
            newItemDto = mapper.toItemDto(itemStorage.create(mapper.toItem(itemDto, ownerId)));
        }
        return newItemDto;
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
        if (userService.getUserById(ownerId) == null) {
            throw new NotFoundException("Пользователь с ID = " + ownerId + " не найден.");
        }
        if (itemDto.getId() == null) {
            itemDto.setId(itemId);
        }
        Item oldItem = itemStorage.getItemById(itemId);
        if (!oldItem.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("У пользователя нет такой вещи.");
        }
        return mapper.toItemDto(itemStorage.update(mapper.toItem(itemDto, ownerId)));
    }

    public ItemDto delete(Long itemId, Long ownerId) {
        Item item = itemStorage.getItemById(itemId);
        if (!item.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("У пользователя нет такой вещи.");
        }
        return mapper.toItemDto(itemStorage.delete(itemId));
    }

    public List<ItemDto> getItemsByOwner(Long ownerId) {
        return itemStorage
                .getItemsByOwner(ownerId)
                .stream()
                .map(mapper::toItemDto)
                .collect(toList());
    }

    public List<ItemDto> getItemsBySearchQuery(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        text = text.toLowerCase();



        //itemStorage.getAll()



        return itemStorage.getItemsBySearchQuery(text)
                .stream()
                .map(mapper::toItemDto)
                .collect(toList());
    }

    public ItemDto getItemById(Long itemId) {
        return mapper.toItemDto(itemStorage.getItemById(itemId));
    }

    public void deleteItemsByOwner(Long ownerId) {
        itemStorage.deleteItemsByOwner(ownerId);
    }

}