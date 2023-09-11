package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен запрос на добавление новой вещи");
        return requestService.create(itemRequestDto, userId);
    }

    @GetMapping("{requestId}")
    public ItemRequestDto findRequestById(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                          @PathVariable Long requestId) {
        log.info("Получен запрос на получение данных о запросе с id = " + requestId);
        return requestService.findById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findRequests(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос на получение списка запросов");
        return requestService.findRequests(userId, from, size);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader(OWNER_ID_HEADER) Long userId) {
        log.info("Получен запрос на получение списка запросов с данными об ответах на них");
        return requestService.findUserRequests(userId);
    }

}