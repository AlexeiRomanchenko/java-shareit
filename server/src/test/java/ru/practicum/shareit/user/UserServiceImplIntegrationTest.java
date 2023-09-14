package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@Rollback
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceImplIntegrationTest {
    private final EntityManager em;
    private final UserService userService;
    private final  UserDto userDto = new UserDto(1L, "email1@ya.com", "user2");
    private final Long userId = 1L;

    @Test
    void testCreate() {
        userService.create(userDto);

        TypedQuery<User> query = em.createQuery("select u from User as u where u.id=:id", User.class);
        User user = query.setParameter("id", userId).getSingleResult();

        assertThat(user.getId(), equalTo(userId));
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void testFindAll() {
        userService.create(userDto);

        List<UserDto> usersDto = userService.findAllUsers();

        assertThat(usersDto.size(), equalTo(1));
        assertThat(usersDto.get(0).getId(), equalTo(userId));
        assertThat(usersDto.get(0).getName(), equalTo(userDto.getName()));
        assertThat(usersDto.get(0).getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void testFindUserById() {
        userService.create(userDto);

        UserDto userDtoTest = userService.findUserById(userId);

        assertThat(userDtoTest.getId(), equalTo(userId));
        assertThat(userDtoTest.getName(), equalTo(userDto.getName()));
        assertThat(userDtoTest.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void testUpdate() {
        userService.create(userDto);

        List<UserDto> usersDto = userService.findAllUsers();

        assertThat(usersDto.size(), equalTo(1));
        assertThat(usersDto.get(0).getId(), equalTo(userId));
        assertThat(usersDto.get(0).getName(), equalTo(userDto.getName()));
        assertThat(usersDto.get(0).getEmail(), equalTo(userDto.getEmail()));

        userDto.setEmail("pochta@ya.ru");

        userService.save(userDto, userId);

        usersDto = userService.findAllUsers();

        assertThat(usersDto.size(), equalTo(1));
        assertThat(usersDto.get(0).getId(), equalTo(userId));
        assertThat(usersDto.get(0).getName(), equalTo(userDto.getName()));
        assertThat(usersDto.get(0).getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void delete() {
        userService.create(userDto);
        userService.delete(userId);

        TypedQuery<User> query = em.createQuery("select u from User as u", User.class);
        List<User> users = query.getResultList();

        assertThat(users.size(), equalTo(0));
    }

}