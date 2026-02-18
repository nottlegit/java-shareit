package ru.practicum.shareit.item.dal;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class ItemRepository {
    private final Map<Long, Item> items;

    public Item save(Item item) {
        if (item.getId() == null) {
            throw new IllegalArgumentException("ID не может быть null");
        }
        items.put(item.getId(), item);
        return item;
    }

    public Optional<Item> findOne(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    public Collection<Item> findAll() {
        return items.values();
    }
}
