package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDto() throws IOException {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .requestId(1L)
                .build();

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .items(List.of(itemDto))
                .created(now)
                .build();

        JsonContent<ItemRequestDto> jsonContent = json.write(itemRequestDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.description")
                .isEqualTo("description");
        assertThat(jsonContent).extractingJsonPathStringValue("$.created")
                .isEqualTo(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(jsonContent).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.items.[0].id").isEqualTo(1);
    }

    @Test
    void testDeserializeItemRequestDto() throws Exception {
        String json = "{\"id\": 1, \"description\": \"description\", \"items\": [{\"id\": 1, \"name\": \"Item\", \"description\": \"Description\", \"available\": true, \"requestId\": 1}], \"created\": \"2022-01-01T12:00:00\"}";

        ItemRequestDto itemRequestDto = this.json.parseObject(json);

        assertThat(itemRequestDto.getId()).isEqualTo(1);
        assertThat(itemRequestDto.getDescription()).isEqualTo("description");
        assertThat(itemRequestDto.getCreated()).isEqualTo(LocalDateTime.parse("2022-01-01T12:00:00"));
        assertThat(itemRequestDto.getItems()).hasSize(1);
        assertThat(itemRequestDto.getItems().get(0).getId()).isEqualTo(1);
        assertThat(itemRequestDto.getItems().get(0).getName()).isEqualTo("Item");
        assertThat(itemRequestDto.getItems().get(0).getDescription()).isEqualTo("Description");
        assertThat(itemRequestDto.getItems().get(0).getAvailable()).isEqualTo(true);
        assertThat(itemRequestDto.getItems().get(0).getRequestId()).isEqualTo(1);
    }

}