package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен запрос на добавление новой вещи");
        return itemRequestClient.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> findRequestById(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                                  @PathVariable Long requestId) {
        log.info("Получен запрос на получение данных о запросе с id = " + requestId);
        return itemRequestClient.getRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findRequests(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос на получение списка запросов");
        return itemRequestClient.getAllItemRequests(userId, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader(OWNER_ID_HEADER) Long userId) {
        log.info("Получен запрос на получение списка запросов с данными об ответах на них");
        return itemRequestClient.getAllRequestsByUserId(userId);
    }

}