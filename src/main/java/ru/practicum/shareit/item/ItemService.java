package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

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

    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item item = findItemById(itemId);

        if (!item.getOwnerId().equals(userId)) {
            throw new AccessDeniedException("Только владелец может редактировать вещь");
        }

        item = itemRepository.save(ItemMapper.updateItemFields(item, itemDto));
        return ItemMapper.toItemDto(item);
    }

    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = findItemById(itemId);

        return ItemMapper.toItemDto(item);
    }

    public Collection<ItemDto> getItemsByOwner(Long userId)  {
        userService.getUserById(userId);

        return itemRepository.findAll().stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .map(ItemMapper::toItemDto)
                .toList();
    }

    public Collection<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.findAll().stream()
                .filter(
                        item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())
                )
                .map(ItemMapper::toItemDto)
                .toList();
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findOne(itemId).orElseThrow(() ->
                new NotFoundException(
                        String.format("Вещь с id: %d не найдена", itemId)
                )
        );
    }
}
