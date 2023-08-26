package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    private final User user = new User(1L, "User", "user@email.com");
    private final UserDto userDto = new UserDto(1L, "User", "user@email.com");
    private final User user2 = new User(2L, "User2", "user2@email.ru");
    private final UserDto userDto2 = new UserDto(2L, "User2", "user2@email.ru");

    @Test
    void createUserTest() {
        Mockito.when(userRepository.save(any()))
                .thenReturn(user);

        assertEquals(userService.create(userDto), userDto);
    }

    @Test
    void findByIdWhenUserIsExistThenReturnedExpectedUser() {
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        assertEquals(userService.findUserById(1L), userDto);
    }

    @Test
    void findByIdWhenUserIsNotExistThenReturnedNotFoundException() {
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception e = assertThrows(NotFoundException.class, () -> userService.findUserById(1L));

        assertEquals(e.getMessage(), String.format("Пользователь с id %d не найден", 1L));
    }

    @Test
    void findAllUsersWhenUsersIsExistThenReturnedExpectedListUsers() {
        Mockito.when(userRepository.findAll())
                .thenReturn(List.of(user, user2));

        List<UserDto> users = userService.findAllUsers();

        assertEquals(users, List.of(userDto, userDto2));
    }

    @Test
    void findAllUsersWhenUsersIsNotExistThenReturnedEmptyList() {
        Mockito.when(userRepository.findAll())
                .thenReturn(new ArrayList<>());

        assertEquals(userService.findAllUsers(), new ArrayList<>());
    }

    @Test
    void updateUserWhenUserIsExistThenReturnedExpectedUpdatedUser() {
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        User updateUser = new User(1L, "updateUser@email.ru", "updateUser");
        UserDto updateUserDto = new UserDto(1L, "updateUser@email.ru", "updateUser");

        Mockito.when(userRepository.save(any()))
                .thenReturn(updateUser);

        assertEquals(userService.save(updateUserDto, 1L), updateUserDto);
    }

    @Test
    void updateUserWhenUserIsNotExistThenReturnedNotFoundException() {
        Mockito.when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception e = assertThrows(NotFoundException.class, () -> userService.findUserById(3L));

        assertEquals(e.getMessage(), String.format("Пользователь с id %d не найден", 3L));
    }

    @Test
    void deleteUser() {
        userService.delete(1L);

        Mockito.verify(userRepository).deleteById(1L);
    }

}