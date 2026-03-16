package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    private BookingDto bookingDto;
    private Booking booking;
    private Item item;
    private User booker;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now().withNano(0);

        booker = new User();
        booker.setId(1L);
        booker.setName("Test User");
        booker.setEmail("test@example.com");

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(booker);

        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItemId(1L);
        bookingDto.setStart(now.plusDays(1));
        bookingDto.setEnd(now.plusDays(2));

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(now.plusDays(1));
        booking.setEnd(now.plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
    }

    @Test
    void toBooking_ShouldConvertBookingDtoToBooking_WhenAllFieldsAreValid() {
        Booking result = BookingMapper.toBooking(bookingDto, item, booker);

        assertNotNull(result);
        assertEquals(bookingDto.getId(), result.getId());
        assertEquals(bookingDto.getStart(), result.getStart());
        assertEquals(bookingDto.getEnd(), result.getEnd());
        assertEquals(item, result.getItem());
        assertEquals(booker, result.getBooker());
        assertNull(result.getStatus());
    }

    @Test
    void toBooking_ShouldReturnNull_WhenBookingDtoIsNull() {
        Booking result = BookingMapper.toBooking(null, item, booker);

        assertNull(result);
    }

    @Test
    void toBooking_ShouldReturnBookingWithNullItem_WhenItemIsNull() {
        Booking result = BookingMapper.toBooking(bookingDto, null, booker);

        assertNotNull(result);
        assertEquals(bookingDto.getId(), result.getId());
        assertEquals(bookingDto.getStart(), result.getStart());
        assertEquals(bookingDto.getEnd(), result.getEnd());
        assertNull(result.getItem());
        assertEquals(booker, result.getBooker());
    }

    @Test
    void toBooking_ShouldReturnBookingWithNullBooker_WhenBookerIsNull() {
        Booking result = BookingMapper.toBooking(bookingDto, item, null);

        assertNotNull(result);
        assertEquals(bookingDto.getId(), result.getId());
        assertEquals(bookingDto.getStart(), result.getStart());
        assertEquals(bookingDto.getEnd(), result.getEnd());
        assertEquals(item, result.getItem());
        assertNull(result.getBooker());
    }

    @Test
    void toResponseDto_ShouldConvertBookingToResponseDto_WhenAllFieldsAreValid() {
        BookingResponseDto result = BookingMapper.toResponseDto(booking);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());

        assertNotNull(result.getBooker());
        assertEquals(booking.getBooker().getId(), result.getBooker().getId());
        assertEquals(booking.getBooker().getName(), result.getBooker().getName());
        assertEquals(booking.getBooker().getEmail(), result.getBooker().getEmail());

        assertNotNull(result.getItem());
        assertEquals(booking.getItem().getId(), result.getItem().getId());
        assertEquals(booking.getItem().getName(), result.getItem().getName());
        assertEquals(booking.getItem().getDescription(), result.getItem().getDescription());
        assertEquals(booking.getItem().getAvailable(), result.getItem().getAvailable());
    }

    @Test
    void toResponseDto_ShouldReturnNull_WhenBookingIsNull() {
        BookingResponseDto result = BookingMapper.toResponseDto(null);

        assertNull(result);
    }

    @Test
    void toResponseDtoCollection_ShouldConvertCollectionOfBookingsToCollectionOfResponseDtos() {
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStart(now.plusDays(3));
        booking2.setEnd(now.plusDays(4));
        booking2.setItem(item);
        booking2.setBooker(booker);
        booking2.setStatus(BookingStatus.APPROVED);

        Collection<Booking> bookings = List.of(booking, booking2);

        Collection<BookingResponseDto> results = BookingMapper.toResponseDtoCollection(bookings);

        assertNotNull(results);
        assertEquals(2, results.size());

        List<BookingResponseDto> resultList = List.copyOf(results);

        BookingResponseDto firstResult = resultList.get(0);
        assertEquals(booking.getId(), firstResult.getId());
        assertEquals(booking.getStatus(), firstResult.getStatus());

        BookingResponseDto secondResult = resultList.get(1);
        assertEquals(booking2.getId(), secondResult.getId());
        assertEquals(booking2.getStatus(), secondResult.getStatus());
    }

    @Test
    void toResponseDtoCollection_ShouldReturnEmptyCollection_WhenInputCollectionIsEmpty() {
        Collection<Booking> emptyBookings = List.of();

        Collection<BookingResponseDto> results = BookingMapper.toResponseDtoCollection(emptyBookings);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void toResponseDto_ShouldSetAllFieldsCorrectly_WhenBookingHasAllFields() {
        booking.setStatus(BookingStatus.REJECTED);

        BookingResponseDto result = BookingMapper.toResponseDto(booking);

        assertNotNull(result);
        assertAll(
                () -> assertEquals(booking.getId(), result.getId()),
                () -> assertEquals(booking.getStart(), result.getStart()),
                () -> assertEquals(booking.getEnd(), result.getEnd()),
                () -> assertEquals(booking.getStatus(), result.getStatus()),
                () -> assertNotNull(result.getBooker()),
                () -> assertNotNull(result.getItem())
        );
    }
}