package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        ItemRequest itemRequest = ItemRequest.builder().description(itemRequestDto.getDescription()).requester(UserMapper.toUser(userService.findUserById(userId))).created(LocalDateTime.now()).build();
        return ItemRequestMapper.toItemRequestDto(requestRepository.save(itemRequest));
    }

    @Transactional(readOnly = true)
    public ItemRequestDto findById(Long userId, Long requestId) {
        ItemRequest itemRequest = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос с Id = " + requestId + "не найден"));

        itemRequest.setItems(itemRepository.findAllByItemRequest(itemRequest));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setRequester(userService.findUserById(userId));
        return itemRequestDto;
    }

    @Transactional(readOnly = true)
    public List<ItemRequestDto> findRequests(Long userId, int from, int size) {
        UserMapper.toUser(userService.findUserById(userId));
        PageRequest page = FromSizeRequest.of(from, size, Sort.by("created").descending());

        List<ItemRequest> itemRequests = requestRepository.findByRequesterIdIsNot(userId, page);

        Map<Long, List<Item>> itemsByItemRequestId = itemRepository.findAllByItemRequestIsNotNull().stream().collect(Collectors.groupingBy(item -> item.getItemRequest().getId()));

        List<ItemRequestDto> list = itemRequests.stream().map(itemRequest -> {
            itemRequest.setItems(itemsByItemRequestId.get(itemRequest.getId()));
            return ItemRequestMapper.toItemRequestDto(itemRequest);
        }).collect(Collectors.toList());

        return list;

    }

    @Transactional(readOnly = true)
    public List<ItemRequestDto> findUserRequests(Long userId) {
        userService.findUserById(userId);
        List<ItemRequest> itemRequests = requestRepository.findByRequesterIdOrderByCreatedDesc(userId);

        Map<Long, List<Item>> itemsByItemRequestId = itemRepository.findAllByItemRequestIsNotNull().stream().collect(Collectors.groupingBy(item -> item.getItemRequest().getId()));

        List<ItemRequestDto> list = itemRequests.stream().map(itemRequest -> {
            itemRequest.setItems(itemsByItemRequestId.get(itemRequest.getId()));
            return ItemRequestMapper.toItemRequestDto(itemRequest);
        }).collect(Collectors.toList());

        return list;
    }

}