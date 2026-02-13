package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.concurrent.atomic.AtomicLong;

@Service
@AllArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final AtomicLong idGenerator;

    public ItemDto createItem(Long userId, ItemDto itemDto) {
        userService.getUserById(userId);

        Item item = ItemMapper.toItem(itemDto, userId);
        item = item.toBuilder().id(idGenerator.getAndIncrement()).build();
        item = itemRepository.save(item);

        return ItemMapper.toItemDto(item);
    }
}
