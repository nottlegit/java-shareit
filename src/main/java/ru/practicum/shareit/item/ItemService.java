package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@AllArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final AtomicLong idGenerator;

    public ItemDto createItem(Long userId, ItemDto itemDto) {
        UserDto user = userService.getUserById(userId);
        log.info("Пользователь найден: {}", user);

        Item item = ItemMapper.toItem(itemDto, userId);
        item = item.toBuilder().id(idGenerator.getAndIncrement()).build();
        item = itemRepository.save(item);

        log.info("Вещь создана с id: {}", item.getId());
        return ItemMapper.toItemDto(item);
    }

    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item item = findItemById(itemId);
        log.info("Найдена вещь: {}, владелец: {}", item, item.getOwnerId());

        if (!item.getOwnerId().equals(userId)) {
            log.warn("Пользователь {} не является владельцем вещи {}. Владелец: {}",
                    userId, itemId, item.getOwnerId());
            throw new AccessDeniedException("Только владелец может редактировать вещь");
        }

        Item.ItemBuilder builder = item.toBuilder();
        boolean updated = false;

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            builder.name(itemDto.getName());
            updated = true;
            log.info("Обновляем name на: {}", itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            builder.description(itemDto.getDescription());
            updated = true;
            log.info("Обновляем description на: {}", itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            builder.available(itemDto.getAvailable());
            updated = true;
            log.info("Обновляем available на: {}", itemDto.getAvailable());
        }

        if (!updated) {
            log.info("Нет полей для обновления");
            return ItemMapper.toItemDto(item);
        }

        item = builder.build();
        item = itemRepository.save(item);
        log.info("Вещь сохранена: {}", item);

        ItemDto result = ItemMapper.toItemDto(item);
        log.info("Результат: {}", result);

        return result;
    }

    public ItemDto getItemById(Long itemId) {
        Item item = findItemById(itemId);

        return ItemMapper.toItemDto(item);
    }

    public Collection<ItemDto> getItemsByOwner(Long userId) {
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
                .filter(Item::getAvailable)
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
