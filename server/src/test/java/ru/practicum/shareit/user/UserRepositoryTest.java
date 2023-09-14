package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryTest {
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final User user = new User(1L, "user@email.com", "User");
    private final User user2 = new User(2L, "user2@email.com", "User2");

    @Test
    public void testSave() {

        Mockito.when(userRepository.save(user))
                .thenReturn(user);

        User savedUser = userRepository.save(user);

        assertEquals(user, savedUser);
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    public void testFindById() {

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userRepository.findById(1L);

        assertTrue(foundUser.isPresent());
        assertEquals(user, foundUser.get());

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    public void testDeleteById() {

        userRepository.deleteById(1L);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void testFindAll() {
        List<User> userList = new ArrayList<>();
        userList.add(user);
        userList.add(user2);

        Mockito.when(userRepository.findAll())
                .thenReturn(userList);

        List<User> foundUsers = userRepository.findAll();

        assertEquals(userList.size(), foundUsers.size());
        assertEquals(userList.get(0), foundUsers.get(0));
        assertEquals(userList.get(1), foundUsers.get(1));
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
    }

}