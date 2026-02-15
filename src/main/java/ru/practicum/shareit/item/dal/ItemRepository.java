package ru.practicum.shareit.item.dal;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class ItemRepository {
    private final HashMap<Long, Item> items;

    public Item save(Item item) {
        items.put(item.getId(), item);

        return item;
    }

    public Optional<Item> findOne(Long itemId) {
        return Optional.of(items.get(itemId));
    }

    public Collection<Item> findAll() {
        return items.values();
    }
}
