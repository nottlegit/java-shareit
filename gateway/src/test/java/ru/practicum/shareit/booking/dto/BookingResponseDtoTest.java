package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingResponseDtoTest {

    @Test
    void shouldCreateBookingResponseDtoUsingBuilder() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserDto booker = UserDto.builder()
                .id(1L)
                .name("test-user-name-1")
                .email("test-email-1@test.com")
                .build();
        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("test-item-name-1")
                .description("test-item-description-1")
                .available(true)
                .build();

        BookingResponseDto dto = BookingResponseDto.builder()
                .id(1L)
                .start(now)
                .end(now.plusDays(1))
                .status(BookingStatus.APPROVED)
                .booker(booker)
                .item(item)
                .build();

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(now, dto.getStart());
        assertEquals(now.plusDays(1), dto.getEnd());
        assertEquals(BookingStatus.APPROVED, dto.getStatus());
        assertEquals(booker, dto.getBooker());
        assertEquals(item, dto.getItem());
    }

    @Test
    void shouldCreateBookingResponseDtoUsingConstructor() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserDto booker = UserDto.builder()
                .id(1L)
                .name("test-user-name-1")
                .email("test-email-1@test.com")
                .build();
        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("test-item-name-1")
                .description("test-item-description-1")
                .available(true)
                .build();

        BookingResponseDto dto = new BookingResponseDto(
                1L, now, now.plusDays(1), BookingStatus.APPROVED, booker, item
        );

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(now, dto.getStart());
        assertEquals(now.plusDays(1), dto.getEnd());
        assertEquals(BookingStatus.APPROVED, dto.getStatus());
        assertEquals(booker, dto.getBooker());
        assertEquals(item, dto.getItem());
    }

    @Test
    void shouldCreateEmptyBookingResponseDtoUsingNoArgsConstructor() {
        BookingResponseDto dto = new BookingResponseDto();

        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getStart());
        assertNull(dto.getEnd());
        assertNull(dto.getStatus());
        assertNull(dto.getBooker());
        assertNull(dto.getItem());
    }

    @Test
    void shouldSetAndGetFields() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserDto booker = UserDto.builder()
                .id(1L)
                .name("test-user-name-1")
                .email("test-email-1@test.com")
                .build();
        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("test-item-name-1")
                .description("test-item-description-1")
                .available(true)
                .build();

        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(1L);
        dto.setStart(now);
        dto.setEnd(now.plusDays(1));
        dto.setStatus(BookingStatus.REJECTED);
        dto.setBooker(booker);
        dto.setItem(item);

        assertEquals(1L, dto.getId());
        assertEquals(now, dto.getStart());
        assertEquals(now.plusDays(1), dto.getEnd());
        assertEquals(BookingStatus.REJECTED, dto.getStatus());
        assertEquals(booker, dto.getBooker());
        assertEquals(item, dto.getItem());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now().withNano(0);

        UserDto booker1 = UserDto.builder()
                .id(1L)
                .name("test-user-name-1")
                .email("test-email-1@test.com")
                .build();

        UserDto booker2 = UserDto.builder()
                .id(2L)
                .name("test-user-name-2")
                .email("test-email-2@test.com")
                .build();

        ItemDto item1 = ItemDto.builder()
                .id(1L)
                .name("test-item-name-1")
                .build();

        ItemDto item2 = ItemDto.builder()
                .id(2L)
                .name("test-item-name-2")
                .build();

        BookingResponseDto dto1 = BookingResponseDto.builder()
                .id(1L)
                .start(now)
                .end(now.plusDays(1))
                .status(BookingStatus.WAITING)
                .booker(booker1)
                .item(item1)
                .build();

        BookingResponseDto dto2 = BookingResponseDto.builder()
                .id(1L)
                .start(now)
                .end(now.plusDays(1))
                .status(BookingStatus.WAITING)
                .booker(booker1)
                .item(item1)
                .build();

        BookingResponseDto dto3 = BookingResponseDto.builder()
                .id(2L)
                .start(now.plusDays(2))
                .end(now.plusDays(3))
                .status(BookingStatus.APPROVED)
                .booker(booker2)
                .item(item2)
                .build();

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testEqualsWithSameObject() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        BookingResponseDto dto = BookingResponseDto.builder()
                .id(1L)
                .start(now)
                .build();

        assertEquals(dto, dto);
    }

    @Test
    void testEqualsWithNull() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        BookingResponseDto dto = BookingResponseDto.builder()
                .id(1L)
                .start(now)
                .build();

        assertNotEquals(null, dto);
    }

    @Test
    void testEqualsWithDifferentClass() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        BookingResponseDto dto = BookingResponseDto.builder()
                .id(1L)
                .start(now)
                .build();

        assertNotEquals("string", dto);
    }

    @Test
    void testToString() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserDto booker = UserDto.builder()
                .id(1L)
                .name("test-user-name-1")
                .build();
        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("test-item-name-1")
                .build();

        BookingResponseDto dto = BookingResponseDto.builder()
                .id(1L)
                .start(now)
                .end(now.plusDays(1))
                .status(BookingStatus.WAITING)
                .booker(booker)
                .item(item)
                .build();

        String toString = dto.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains(now.toString()));
        assertTrue(toString.contains(now.plusDays(1).toString()));
        assertTrue(toString.contains("WAITING"));
        assertTrue(toString.contains("test-user-name-1"));
        assertTrue(toString.contains("test-item-name-1"));
    }

    @Test
    void testCanEqual() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        BookingResponseDto dto1 = BookingResponseDto.builder()
                .id(1L)
                .start(now)
                .build();
        BookingResponseDto dto2 = BookingResponseDto.builder()
                .id(1L)
                .start(now)
                .build();

        assertTrue(dto1.canEqual(dto2));
    }

    @Test
    void shouldHandleNullFields() {
        BookingResponseDto dto = new BookingResponseDto();

        assertNull(dto.getId());
        assertNull(dto.getStart());
        assertNull(dto.getEnd());
        assertNull(dto.getStatus());
        assertNull(dto.getBooker());
        assertNull(dto.getItem());

        dto.setId(null);
        dto.setStart(null);
        dto.setEnd(null);
        dto.setStatus(null);
        dto.setBooker(null);
        dto.setItem(null);

        assertNull(dto.getId());
        assertNull(dto.getStart());
        assertNull(dto.getEnd());
        assertNull(dto.getStatus());
        assertNull(dto.getBooker());
        assertNull(dto.getItem());
    }

    @Test
    void shouldCreateDtoWithNullBooker() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("test-item-name-1")
                .build();

        BookingResponseDto dto = BookingResponseDto.builder()
                .id(1L)
                .start(now)
                .end(now.plusDays(1))
                .status(BookingStatus.WAITING)
                .booker(null)
                .item(item)
                .build();

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertNull(dto.getBooker());
        assertNotNull(dto.getItem());
    }

    @Test
    void shouldCreateDtoWithNullItem() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserDto booker = UserDto.builder()
                .id(1L)
                .name("test-user-name-1")
                .build();

        BookingResponseDto dto = BookingResponseDto.builder()
                .id(1L)
                .start(now)
                .end(now.plusDays(1))
                .status(BookingStatus.WAITING)
                .booker(booker)
                .item(null)
                .build();

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertNotNull(dto.getBooker());
        assertNull(dto.getItem());
    }
}