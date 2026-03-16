package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

@UtilityClass
public class BookingMapper {

    public static Booking toBooking(BookingDto bookingDto, Item item, User booker) {
        if (bookingDto == null) {
            return null;
        }

        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);

        return booking;
    }

    public static BookingResponseDto toResponseDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        UserDto userDto = UserMapper.mapToUserDto(booking.getBooker());
        ItemDto itemDto = ItemMapper.toItemDto(booking.getItem());

        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(userDto)
                .item(itemDto)
                .build();
    }

    public static Collection<BookingResponseDto> toResponseDtoCollection(Collection<Booking> bookings) {
        if (bookings == null) {
            return null;
        }

        return bookings.stream()
                .map(BookingMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
