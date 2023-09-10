package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Controller
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor
public class ItemController {
    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(OWNER_ID_HEADER) Long ownerId,
                                         @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на добавление вещи по ее владельцу с ID = {}", ownerId);
        return itemClient.addItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> save(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                       @PathVariable long itemId,
                                       @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на обновление вещей с ID = {}", itemId);
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId) {
        log.info("Получен запрос на удаление всех вещей с ID = {}", itemId);
        itemClient.deleteItem(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                          @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                          @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос на получение всех вещей владельца с ID = {}", userId);
        return itemClient.getItemsByOwnerId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                              @RequestParam String text,
                                              @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Получен запрос на поиск вещей со следующим текстом: {}", text);
        return itemClient.getItemsByTextOfQuery(userId, text, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@PathVariable Long itemId, @RequestHeader(OWNER_ID_HEADER) Long userId) {
        log.info("Получен запрос на получение вещи с ID = {}", itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                                @PathVariable Long itemId,
                                                @Valid @RequestBody CommentDto commentDto) {
        log.info("Получен запрос на добавление комментария");
        return itemClient.addComment(itemId, userId, commentDto);
    }

}