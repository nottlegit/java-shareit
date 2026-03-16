package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemRequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private Collection<ItemRequestItemDto> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class ItemRequestItemDto {
        private Long id;
        private String name;
        private Long ownerId;
        private Long requestId;
    }
}
