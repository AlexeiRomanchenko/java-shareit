package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ItemRequestRepositoryTest {
    private final User user = new User(1L, "user@email.com", "User");
    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .requester(user)
            .description("description")
            .build();

    private final ItemRequest itemRequest2 = ItemRequest.builder()
            .id(2L)
            .requester(user)
            .description("description2")
            .build();

    @Test
    public void findByRequesterIdOrderByCreatedDescTest() {
        ItemRequestRepository itemRequestRepository = mock(ItemRequestRepository.class);

        Long userId = 1L;
        List<ItemRequest> itemRequests = List.of(itemRequest, itemRequest2);

        Mockito.when(itemRequestRepository.findByRequesterIdOrderByCreatedDesc(eq(userId)))
                .thenReturn(itemRequests);

        List<ItemRequest> result = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId);

        assertEquals(2, result.size());
        verify(itemRequestRepository).findByRequesterIdOrderByCreatedDesc(eq(userId));
    }

    @Test
    public void findByRequesterIdIsNotTest() {
        ItemRequestRepository itemRequestRepository = mock(ItemRequestRepository.class);

        Long userId = 1L;
        Pageable page = mock(Pageable.class);
        List<ItemRequest> itemRequests = List.of(itemRequest, itemRequest2);

        Mockito.when(itemRequestRepository.findByRequesterIdIsNot(eq(userId), any(Pageable.class)))
                .thenReturn(itemRequests);

        List<ItemRequest> result = itemRequestRepository.findByRequesterIdIsNot(userId, page);

        assertEquals(2, result.size());
        verify(itemRequestRepository).findByRequesterIdIsNot(eq(userId), eq(page));
    }

}