package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    private final UserDto userDto = new UserDto(1L, "user@email.com", "User");

    @Test
    void createUserWhenUserDtoValidThenReturnedStatusIsOk() throws Exception {
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService).create(userDto);
    }

    @Test
    void createUserWhenUserDtoNotValidThenReturnedBadRequest() throws Exception {
        UserDto userDto2 = new UserDto(2L, "", "user2@email.ru");

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto2))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(userDto);
    }

    @Test
    void findByIdWhenUserIsExistThenReturnedStatusIsOk() throws Exception {
        Mockito.when(userService.checkFindUserById(anyLong()))
                .thenReturn(userDto);

        String result = mvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, mapper.writeValueAsString(userDto));
    }

    @Test
    void findByIdWhenUserIsNotExistThenReturnedStatusIsNotFound() throws Exception {
        Mockito.when(userService.checkFindUserById(100L))
                .thenThrow(new NotFoundException(String.format("Пользователь с id %d не найден", 1L)));

        mvc.perform(get("/users/{id}", 100L))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void findAllUsersTest() throws Exception {
        Mockito.when(userService.findAllUsers())
                .thenReturn(List.of(userDto));

        String result = mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, mapper.writeValueAsString(List.of(userDto)));
    }

    @Test
    void updateUserTest() throws Exception {
        UserDto updateUserDto = UserDto.builder()
                .id(1L)
                .name("updateUser")
                .email("updateuser@email.com")
                .build();

        when(userService.save(any(), anyLong()))
                .thenReturn(updateUserDto);

        mvc.perform(patch("/users/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(updateUserDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService).save(updateUserDto, 1L);
    }

    @Test
    void deleteUserTest() throws Exception {
        mvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isOk());

        verify(userService).delete(1L);
    }

}