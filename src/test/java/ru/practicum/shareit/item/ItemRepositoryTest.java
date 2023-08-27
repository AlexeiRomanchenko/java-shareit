package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ItemRepositoryTest {
    ItemRepository itemRepository = Mockito.mock(ItemRepository.class);

    User user = new User(1L, "user@email.com", "User");
    ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .requester(user)
            .description("description")
            .build();
    private final Item item = Item.builder()
            .id(1L)
            .name("ItemName")
            .description("description")
            .available(true)
            .itemRequest(itemRequest)
            .ownerId(1L)
            .build();

    private final Item item2 = Item.builder()
            .id(2L)
            .name("ItemNam2e")
            .description("description2")
            .available(true)
            .ownerId(1L)
            .build();

    @Test
    public void testFindAllByOwnerId() {
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        Long userId = 1L;
        Pageable page = PageRequest.of(0, 10);

        List<Item> expectedItems = List.of(item, item2);

        Mockito.when(itemRepository.findAllByOwnerId(Mockito.eq(userId), Mockito.eq(page)))
                .thenReturn(expectedItems);

        List<Item> actualItems = itemRepository.findAllByOwnerId(userId, page);
        assertEquals(2, actualItems.size());
        assertEquals(expectedItems, actualItems);
    }

    @Test
    public void testSearchAvailableItems() {
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        String text = "example";
        Pageable page = PageRequest.of(0, 10);

        List<Item> expectedItems = List.of(item, item2);
        Mockito.when(itemRepository.searchAvailableItems(text, page))
                .thenReturn(expectedItems);

        List<Item> actualItems = itemRepository.searchAvailableItems(text, page);

        assertEquals(expectedItems, actualItems);
    }

    @Test
    public void testFindAllByItemRequest() {

        List<Item> expectedItems = List.of(item, item2);

        Mockito.when(itemRepository.findAllByItemRequest(itemRequest))
                .thenReturn(expectedItems);

        List<Item> actualItems = itemRepository.findAllByItemRequest(itemRequest);

        assertEquals(expectedItems, actualItems);
    }

    @Test
    public void testFindAllByItemRequestIsNotNull() {
        List<Item> expectedItems = List.of(item);

        Mockito.when(itemRepository.findAllByItemRequestIsNotNull())
                .thenReturn(expectedItems);

        List<Item> actualItems = itemRepository.findAllByItemRequestIsNotNull();

        assertEquals(expectedItems, actualItems);
    }

}