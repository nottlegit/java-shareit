package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {
    public static Item toItem(ItemDto itemDto, Long ownerId) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .ownerId(ownerId)
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item updateItemFields(Item item, ItemDto itemDto) {
        Item.ItemBuilder builder = item.toBuilder();

        if (itemDto.hasName()) {
            builder.name(itemDto.getName());
        }
        if (itemDto.hasDescription()) {
            builder.description(itemDto.getDescription());
        }
        if (itemDto.hasAvailable()) {
            builder.available(itemDto.getAvailable());
        }

        return builder.build();
    }
}
