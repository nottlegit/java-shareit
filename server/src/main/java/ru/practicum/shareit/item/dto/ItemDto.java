package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.Collection;

@Data
@Builder(toBuilder = true)
public class ItemDto {
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @NotNull(message = "Статус должен быть указан")
    private Boolean available;

    private Collection<CommentDto> comments;
    private BookingShort lastBooking;
    private BookingShort nextBooking;
    private Long requestId;
}
