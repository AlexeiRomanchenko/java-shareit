package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.interfaces.UserStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final UserMapper mapper;

    public UserDto create(UserDto userDto) {
        User user = mapper.toUser(userDto);

        Long idFromDbByEmail = getUserIdByEmail(user);
        if (idFromDbByEmail != null) {
            throw new AlreadyExistsException("Пользователь с e-mail = " + user.getEmail() + " уже существует.");
        }
        return mapper.toUserDto(userStorage.create(user));
    }

    public UserDto update(UserDto userDto, Long id) {
        userDto.setId(id);

        userDto = checkOnNullNameAndEmail(userDto, id);

        User user = mapper.toUser(userDto);
        checkUserOnNull(user.getId());

        if (user.getId() == null) {
            throw new ValidationException("ID пользователя не может быть пустым.");
        }

        final Long idFromDbByEmail = getUserIdByEmail(user);
        if (idFromDbByEmail != null && !user.getId().equals(idFromDbByEmail)) {
            throw new AlreadyExistsException("Пользователь с e-mail = " + user.getEmail() + " уже существует.");
        }

        List<String> emails = userStorage.getEmails();

        if (!emails.contains(user.getEmail())) {
            userStorage.deleteEmail(getUserById(id).getEmail());
            userStorage.addEmail(user.getEmail());
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
        checkUserOnNull(id);
        return mapper.toUserDto(userStorage.getUserById(id));
    }

    public void checkUserOnNull(Long id) {
        Optional.ofNullable(userStorage.getUserById(id)).orElseThrow(
                () -> {
                    throw new NotFoundException("Пользователь с id " + id + " не найден");
                });
    }

    public Long getUserIdByEmail(User inUser) {
        String inputEmail = inUser.getEmail();

        if (inputEmail == null) {
            return null;
        }

        List<String> emails = userStorage.getEmails();

        if (emails.contains(inputEmail)) {
            List<User> users = userStorage.getUsers();

            for (User user : users) {
                String email = user.getEmail();
                if (email.equals(inputEmail)) {
                    return user.getId();
                }
            }

        }
        return null;
    }

    public UserDto checkOnNullNameAndEmail(UserDto userDto, Long id) {
        if (userDto.getName() == null) {
            userDto.setName(userStorage.getUserById(id).getName());
        }
        if (userDto.getEmail() == null) {
            userDto.setEmail(userStorage.getUserById(id).getEmail());
        }
        return userDto;
    }

}