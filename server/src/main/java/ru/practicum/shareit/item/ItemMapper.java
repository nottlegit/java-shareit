package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@UtilityClass
public class ItemMapper {
    public static Item toItem(ItemDto itemDto, User owner) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
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

    public static ItemDto toItemDto(Item item, Long userId, Collection<Booking> bookings, Collection<Comment> comments) {
        ItemDto itemDto = toItemDto(item);
        ItemDto.ItemDtoBuilder builder = itemDto.toBuilder();

        builder.comments(comments != null
                ? comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList())
                : new ArrayList<>());

        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime localDateTimeNow = LocalDateTime.now();

            Booking last = bookings.stream()
                    .filter(booking -> booking.getEnd().isBefore(localDateTimeNow))
                    .max(Comparator.comparing(Booking::getEnd))
                    .orElse(null);

            Booking next = bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(localDateTimeNow))
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);

            builder.lastBooking(last != null ? mapToBookingShort(last) : null);
            builder.nextBooking(next != null ? mapToBookingShort(next) : null);
        }

        return builder.build();
    }

    private BookingShort mapToBookingShort(Booking booking) {
        return BookingShort.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }
}
