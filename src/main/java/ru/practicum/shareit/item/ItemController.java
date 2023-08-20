package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import java.util.Collection;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader(OWNER_ID_HEADER) Long ownerId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на добавление вещи по ее владельцу с ID = {}", ownerId);
        return itemService.create(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto save(@RequestHeader(OWNER_ID_HEADER) Long userId, @PathVariable long itemId,
                        @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на обновление вещей с ID = {}", itemId);
        return itemService.save(itemDto, itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId) {
        log.info("Получен запрос на удаление всех вещей с ID = {}", itemId);
        itemService.deleteById(itemId);
    }

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                 @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос на получение всех вещей владельца с ID = {}", userId);
        return itemService.findUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text,
                                      @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                      @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен запрос на поиск вещей со следующим текстом: {}", text);
        return itemService.search(text, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@RequestHeader(OWNER_ID_HEADER) Long userId, @PathVariable Long itemId) {
        log.info("Получен запрос на получение вещи с ID = {}", itemId);
        return itemService.findItemById(itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(OWNER_ID_HEADER) Long userId,
                                    @PathVariable Long itemId,
                                    @Valid @RequestBody CommentDto commentDto) {
        log.info("Получен запрос на добавление комментария");
        return itemService.addComment(itemId, userId, commentDto);
    }

}