package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @InjectMocks
    private ItemRequestService requestService;
    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;

    private final User user = new User(1L, "user@email.com", "User");
    private final UserDto userDto = new UserDto(1L, "user@email.com", "User");
    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .requester(user)
            .description("description")
            .build();
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("description")
            .requester(userDto)
            .items(new ArrayList<>())
            .build();

    @Test
    void createRequestWhenUserIsExistThenReturnedExpectedRequest() {
        Mockito.when(userService.findUserById(anyLong()))
                .thenReturn(userDto);

        Mockito.when(requestRepository.save(any()))
                .thenReturn(itemRequest);

        assertEquals(requestService.create(itemRequestDto, 1L), itemRequestDto);
    }

    @Test
    void createRequestWhenUserIsNotExistThenReturnedNotFoundException() {
        Mockito.when(userService.findUserById(anyLong()))
                .thenThrow(new NotFoundException(String.format("Пользователь с id = %d не найден.", 1L)));

        Exception e = assertThrows(NotFoundException.class,
                () -> requestService.create(itemRequestDto, 100L));

        assertEquals(e.getMessage(), String.format("Пользователь с id = %d не найден.", 1L));
    }

    @Test
    void findByIdWhenRequestIsValidThenReturnedExpectedRequest() {
        Mockito.when(userService.findUserById(anyLong()))
                .thenReturn(userDto);
        Mockito.when(requestRepository.findById(any()))
                .thenReturn(Optional.ofNullable(itemRequest));
        Mockito.when(itemRepository.findAllByItemRequest(any()))
                .thenReturn(new ArrayList<>());

        assertEquals(requestService.findById(1L, 1L), itemRequestDto);
    }

    @Test
    void findByIdWhenRequestIsNotExistThenReturnedNotFoundException() {
        Mockito.when(requestRepository.findById(anyLong()))
                .thenThrow(new NotFoundException(String.format("Запрос с id  = %d не найден", 1L)));

        Exception e = assertThrows(NotFoundException.class,
                () -> requestService.findById(1L, 1L));

        assertEquals(e.getMessage(), String.format("Запрос с id  = %d не найден", 1L));
    }

    @Test
    void findAllRequestsWhenParamsIsExistThenReturnedExpectedListRequests() {
        Mockito.when(userService.findUserById(anyLong()))
                .thenReturn(userDto);

        Mockito.when(requestRepository.findByRequesterIdIsNot(anyLong(), any()))
                .thenReturn(List.of(itemRequest));

        assertEquals(requestService.findRequests(1L, 1, 1), List.of(itemRequestDto));
    }

    @Test
    void findAllRequestsWhenUserIsNotExistThenReturnedNotFoundException() {
        Mockito.when(userService.findUserById(anyLong()))
                .thenThrow(new NotFoundException(String.format("Пользователь с id = %d не найден.", 1L)));

        Exception e = assertThrows(NotFoundException.class,
                () -> requestService.findRequests(1L, 1, 1));

        assertEquals(e.getMessage(), String.format("Пользователь с id = %d не найден.", 1L));
    }

    @Test
    void findAllUserRequestsWhenUserIsExistThenReturnedExpectedListRequests() {
        Mockito.when(userService.findUserById(anyLong()))
                .thenReturn(userDto);

        Mockito.when(requestRepository.findByRequesterIdOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(itemRequest));

        assertEquals(requestService.findUserRequests(1L), List.of(itemRequestDto));
    }

    @Test
    void findAllUserRequestsWhenUserIsNotExistThenReturnedNotFoundException() {
        Mockito.when(userService.findUserById(anyLong()))
                .thenThrow(new NotFoundException(String.format("Пользователь с id = %d не найден.", 1L)));

        Exception e = assertThrows(NotFoundException.class,
                () -> requestService.findUserRequests(1L));

        assertEquals(e.getMessage(), String.format("Пользователь с id = %d не найден.", 1L));
    }

}