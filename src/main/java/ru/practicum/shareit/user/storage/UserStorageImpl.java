package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.interfaces.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserStorageImpl implements UserStorage {

    private final Map<Long, User> users;
    private final HashSet<String> emails;
    private Long currentId;

    public UserStorageImpl() {
        currentId = 0L;
        users = new HashMap<>();
        emails = new HashSet<>();
    }

    @Override
    public User create(User user) {
        user.setId(++currentId);
        emails.add(user.getEmail());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!emails.contains(user.getEmail())) {
            emails.remove(getUserById(user.getId()).getEmail());
            emails.add(user.getEmail());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User delete(Long userId) {
        emails.remove(getUserById(userId).getEmail());
        return users.remove(userId);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Long userId) {
        return users.get(userId);
    }

    @Override
    public boolean isExistUserInDb(Long id) {
        return users.containsKey(id);
    }

    @Override
    public Long getUserIdByEmail(User inUser) {
        String inputEmail = inUser.getEmail();

        if (inputEmail == null) {
            return null;
        }

        if (emails.contains(inputEmail)) {
            for (User user : users.values()) {
                String email = user.getEmail();
                if (email.equals(inputEmail)) {
                    return user.getId();
                }
            }

        }
        return null;
    }

}