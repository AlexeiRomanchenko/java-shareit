package ru.practicum.shareit.user.service;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.interfaces.UserStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final UserMapper mapper;

    public UserDto create(UserDto userDto) {
        User user = mapper.toUser(userDto);

        Long idFromDbByEmail = userStorage.getUserIdByEmail(user.getEmail());
        if (idFromDbByEmail != null) {
            throw new AlreadyExistsException("Пользователь с e-mail = " + user.getEmail() + " уже существует.");
        }
        return mapper.toUserDto(userStorage.create(user));
    }

    public UserDto update(UserDto userDto, Long id) {
        userDto.setId(id);
        if (userDto.getName() == null) {
            userDto.setName(userStorage.getUserById(id).getName());
        }
        if (userDto.getEmail() == null) {
            userDto.setEmail(userStorage.getUserById(id).getEmail());
        }
        User user = mapper.toUser(userDto);
        if (userStorage.getUserById(user.getId()) == null) {
            throw new NotFoundException("Пользователь с ID = " + user.getId() + " не найден.");
        }
        if (user.getId() == null) {
            throw new ValidationException("ID пользователя не может быть пустым.");
        }
        final Long idFromDbByEmail = userStorage.getUserIdByEmail(user.getEmail());
        if (idFromDbByEmail != null && !user.getId().equals(idFromDbByEmail)) {
            throw new AlreadyExistsException("User with e-mail=" + user.getEmail() + " already exists.");
        }
        User updateUser = userStorage.update(user);
        return mapper.toUserDto(updateUser);
    }

    public UserDto delete(Long userId) {
        if (userId == null) {
            throw new ValidationException("ID пользователя не может быть пустым.");
        }
        if (!userStorage.isExistUserInDb(userId)) {
            throw new NotFoundException("Пользователь с ID = " + userId + " не найден.");
        }
        return mapper.toUserDto(userStorage.delete(userId));
    }

    public List<UserDto> getUsers() {
        return userStorage.getUsers().stream()
                .map(mapper::toUserDto)
                .collect(toList());
    }

    public UserDto getUserById(Long id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID = " + id + " не найден.");
        }
        return mapper.toUserDto(userStorage.getUserById(id));
    }

}