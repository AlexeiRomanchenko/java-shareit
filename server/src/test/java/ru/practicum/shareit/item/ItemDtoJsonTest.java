package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testItemDto() throws IOException {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        JsonContent<ItemDto> jsonContent = json.write(itemDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("Item");
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("Description");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }

    @Test
    void testDeserializeItemDto() throws IOException {
        String jsonStr = "{\"id\": 1, \"name\": \"Item\", \"description\": \"Description\", \"available\": true}";
        ItemDto itemDto = json.parseObject(jsonStr);

        assertThat(itemDto.getId()).isEqualTo(1);
        assertThat(itemDto.getName()).isEqualTo("Item");
        assertThat(itemDto.getDescription()).isEqualTo("Description");
        assertThat(itemDto.getAvailable()).isEqualTo(true);
    }

}