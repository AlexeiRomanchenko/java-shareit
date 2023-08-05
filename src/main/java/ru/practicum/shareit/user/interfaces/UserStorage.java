package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User create(User user);

    User update(User user);

    User delete(Long userId);

    List<User> getUsers();

    List<String> getEmails();

    void addEmail(String email);

    void deleteEmail(String email);

    User getUserById(Long userId);

    boolean isExistUserInDb(Long id);

}