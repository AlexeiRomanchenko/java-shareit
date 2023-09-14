package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long userId, Pageable page);

    @Query("SELECT i FROM Item i "
            + "WHERE upper(i.name) like upper(concat('%', :text, '%')) "
            + "OR upper(i.description) like upper(concat('%', :text, '%')) "
            + "AND i.available = true")

    List<Item> searchAvailableItems(@Param("text") String text, Pageable page);

    List<Item> findAllByItemRequest(ItemRequest itemRequest);

    List<Item> findAllByItemRequestIsNotNull();

}