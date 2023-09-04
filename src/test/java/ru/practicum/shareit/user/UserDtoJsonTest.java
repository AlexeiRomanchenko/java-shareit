package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UserDtoJsonTest {
    @Autowired
    private JacksonTester<UserDto> json;
    private final UserDto userDto = new UserDto(1L, "email@ya.com", "user");

    @Test
    void testSerializeUserDto() throws Exception {
        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("email@ya.com");
    }

    @Test
    void testDeserializeUserDto() throws Exception {
        String jsonContent = "{\"id\": 1, \"email\": \"email@ya.com\", \"name\": \"user\"}";

        UserDto expectedUserDto = new UserDto(1L, "email@ya.com", "user");
        UserDto result = json.parseObject(jsonContent);

        assertThat(result).isEqualTo(expectedUserDto);
    }

}