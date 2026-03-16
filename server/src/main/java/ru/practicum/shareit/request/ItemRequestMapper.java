package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Component
public class ItemRequestMapper {
    public static ItemRequestDto toDto(ItemRequest request) {
        if (request == null) {
            return null;
        }

        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .build();
    }

    public static ItemRequest toEntity(ItemRequestCreateDto dto) {
        if (dto == null) {
            return null;
        }

        return ItemRequest.builder()
                .description(dto.getDescription())
                .build();
    }

    public static ItemRequest toEntity(ItemRequestCreateDto dto, User requester) {
        if (dto == null) {
            return null;
        }

        return ItemRequest.builder()
                .description(dto.getDescription())
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
    }
}
