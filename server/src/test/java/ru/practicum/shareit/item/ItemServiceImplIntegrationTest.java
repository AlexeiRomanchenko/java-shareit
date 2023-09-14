package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@Rollback
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceImplIntegrationTest {
    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestService requestService;
    private final UserDto userDto = new UserDto(1L, "email1@ya.com", "user1");
    private final UserDto userDto2 = new UserDto(2L, "pochta1@ya.com", "user2");
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Item")
            .description("Description")
            .available(true)
            .requestId(1L)
            .build();

    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .requester(userDto)
            .description("Описание")
            .build();

    @Test
    void testCreateItem() {
        Long itemId = 1L;
        Long ownerId = 1L;
        Long requesterId = 2L;

        userService.create(userDto);
        userService.create(userDto2);
        requestService.create(itemRequestDto, requesterId);
        itemService.create(ownerId, itemDto);

        TypedQuery<Item> query = em.createQuery("select i from Item as i where i.id=:id", Item.class);
        Item item = query.setParameter("id", itemId).getSingleResult();


        assertThat(item, notNullValue());
        assertThat(item.getId(), equalTo(itemId));
        assertThat(item.getOwnerId(), notNullValue());
        assertThat(item.getOwnerId(), equalTo(ownerId));
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));

        UserDto userTest = userService.findUserById(item.getOwnerId());

        assertThat(userTest.getName(), equalTo(userDto.getName()));
        assertThat(userTest.getEmail(), equalTo(userDto.getEmail()));
        assertThat(item.getItemRequest(), notNullValue());
        assertThat(item.getItemRequest().getId(), equalTo(1L));
        assertThat(item.getItemRequest().getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(item.getItemRequest().getRequester().getId(), equalTo(requesterId));
    }

}