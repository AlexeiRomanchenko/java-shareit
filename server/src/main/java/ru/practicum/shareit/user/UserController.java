package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("Получен запрос на добавление нового пользователя.");
        return userService.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto save(@RequestBody UserDto userDto, @PathVariable long userId) {
        log.info("Получен запрос на обновление пользователя с ID = {}", userId);
        return userService.save(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Получен запрос на удаление пользователя с ID = {}", userId);
        userService.delete(userId);
    }

    @GetMapping
    public List<UserDto> findAll() {
        log.info("Получен запрос на получение списка всех пользователей");
        return userService.findAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable long userId) {
        log.info("Получен запрос на получение пользователя с ID = {}", userId);
        return userService.checkFindUserById(userId);
    }

}