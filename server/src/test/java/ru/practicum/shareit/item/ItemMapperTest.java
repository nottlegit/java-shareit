package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    private User owner;
    private User booker;
    private Item item;
    private Booking lastBooking;
    private Booking nextBooking;
    private Comment comment;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now().withNano(0);

        owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@test.com");

        booker = new User();
        booker.setId(2L);
        booker.setName("Booker");
        booker.setEmail("booker@test.com");

        item = Item.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .build();

        lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setStart(now.minusDays(5));
        lastBooking.setEnd(now.minusDays(1));
        lastBooking.setItem(item);
        lastBooking.setBooker(booker);
        lastBooking.setStatus(BookingStatus.APPROVED);

        nextBooking = new Booking();
        nextBooking.setId(2L);
        nextBooking.setStart(now.plusDays(1));
        nextBooking.setEnd(now.plusDays(3));
        nextBooking.setItem(item);
        nextBooking.setBooker(booker);
        nextBooking.setStatus(BookingStatus.APPROVED);

        comment = new Comment();
        comment.setId(1L);
        comment.setText("Great item!");
        comment.setItem(item);
        comment.setAuthor(booker);
        comment.setCreated(now);
    }

    @Test
    void toItem_ShouldConvertItemDtoToItem_WhenAllFieldsAreValid() {
        ItemDto itemDto = ItemDto.builder()
                .name("New Drill")
                .description("New powerful drill")
                .available(true)
                .build();

        Item result = ItemMapper.toItem(itemDto, owner);

        assertNotNull(result);
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());
        assertEquals(owner, result.getOwner());
        assertNull(result.getId());
    }

    @Test
    void toItem_ShouldReturnItemWithNullOwner_WhenOwnerIsNull() {
        ItemDto itemDto = ItemDto.builder()
                .name("New Drill")
                .description("New powerful drill")
                .available(true)
                .build();

        Item result = ItemMapper.toItem(itemDto, null);

        assertNotNull(result);
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());
        assertNull(result.getOwner());
    }

    @Test
    void toItemDto_ShouldConvertItemToItemDto_WhenAllFieldsAreValid() {
        ItemDto result = ItemMapper.toItemDto(item);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertNull(result.getComments());
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    @Test
    void toItemDto_WithBookingsAndComments_ShouldIncludeLastAndNextBookings_WhenUserIsOwner() {
        Collection<Booking> bookings = List.of(lastBooking, nextBooking);
        Collection<Comment> comments = List.of(comment);

        ItemDto result = ItemMapper.toItemDto(item, owner.getId(), bookings, comments);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());

        assertNotNull(result.getComments());
        assertEquals(1, result.getComments().size());
        assertEquals(comment.getId(), result.getComments().stream().toList().getFirst().getId());
        assertEquals(comment.getText(), result.getComments().stream().toList().getFirst().getText());
        assertEquals(comment.getAuthor().getName(), result.getComments().stream().toList().getFirst().getAuthorName());

        BookingShort lastBookingShort = result.getLastBooking();
        assertNotNull(lastBookingShort);
        assertEquals(lastBooking.getId(), lastBookingShort.getId());
        assertEquals(lastBooking.getBooker().getId(), lastBookingShort.getBookerId());
        assertEquals(lastBooking.getStart(), lastBookingShort.getStart());
        assertEquals(lastBooking.getEnd(), lastBookingShort.getEnd());

        BookingShort nextBookingShort = result.getNextBooking();
        assertNotNull(nextBookingShort);
        assertEquals(nextBooking.getId(), nextBookingShort.getId());
        assertEquals(nextBooking.getBooker().getId(), nextBookingShort.getBookerId());
        assertEquals(nextBooking.getStart(), nextBookingShort.getStart());
        assertEquals(nextBooking.getEnd(), nextBookingShort.getEnd());
    }

    @Test
    void toItemDto_WithBookingsAndComments_ShouldNotIncludeBookings_WhenUserIsNotOwner() {
        Collection<Booking> bookings = List.of(lastBooking, nextBooking);
        Collection<Comment> comments = List.of(comment);

        ItemDto result = ItemMapper.toItemDto(item, booker.getId(), bookings, comments);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());

        assertNotNull(result.getComments());
        assertEquals(1, result.getComments().size());

        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    @Test
    void toItemDto_WithOnlyPastBookings_ShouldIncludeOnlyLastBooking() {
        Booking pastBooking1 = new Booking();
        pastBooking1.setId(3L);
        pastBooking1.setStart(now.minusDays(10));
        pastBooking1.setEnd(now.minusDays(8));
        pastBooking1.setItem(item);
        pastBooking1.setBooker(booker);
        pastBooking1.setStatus(BookingStatus.APPROVED);

        Booking pastBooking2 = new Booking();
        pastBooking2.setId(4L);
        pastBooking2.setStart(now.minusDays(7));
        pastBooking2.setEnd(now.minusDays(5));
        pastBooking2.setItem(item);
        pastBooking2.setBooker(booker);
        pastBooking2.setStatus(BookingStatus.APPROVED);

        Collection<Booking> bookings = List.of(pastBooking1, pastBooking2, lastBooking);
        Collection<Comment> comments = List.of();

        ItemDto result = ItemMapper.toItemDto(item, owner.getId(), bookings, comments);

        assertNotNull(result);

        BookingShort lastBookingShort = result.getLastBooking();
        assertNotNull(lastBookingShort);
        assertEquals(lastBooking.getId(), lastBookingShort.getId());

        assertNull(result.getNextBooking());
    }

    @Test
    void toItemDto_WithOnlyFutureBookings_ShouldIncludeOnlyNextBooking() {
        Booking futureBooking1 = new Booking();
        futureBooking1.setId(5L);
        futureBooking1.setStart(now.plusDays(4));
        futureBooking1.setEnd(now.plusDays(6));
        futureBooking1.setItem(item);
        futureBooking1.setBooker(booker);
        futureBooking1.setStatus(BookingStatus.APPROVED);

        Collection<Booking> bookings = List.of(nextBooking, futureBooking1);
        Collection<Comment> comments = List.of();

        ItemDto result = ItemMapper.toItemDto(item, owner.getId(), bookings, comments);

        assertNotNull(result);

        assertNull(result.getLastBooking());

        BookingShort nextBookingShort = result.getNextBooking();
        assertNotNull(nextBookingShort);
        assertEquals(nextBooking.getId(), nextBookingShort.getId());
    }

    @Test
    void toItemDto_WithEmptyBookings_ShouldNotIncludeAnyBookings() {
        Collection<Booking> bookings = List.of();
        Collection<Comment> comments = List.of();

        ItemDto result = ItemMapper.toItemDto(item, owner.getId(), bookings, comments);

        assertNotNull(result);
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
        assertNotNull(result.getComments());
        assertTrue(result.getComments().isEmpty());
    }

    @Test
    void toItemDto_WithNullComments_ShouldSetEmptyCommentsList() {
        Collection<Booking> bookings = List.of();

        ItemDto result = ItemMapper.toItemDto(item, owner.getId(), bookings, null);

        assertNotNull(result);
        assertNotNull(result.getComments());
        assertTrue(result.getComments().isEmpty());
    }

    @Test
    void toItemDto_WithBookingsAndNullComments_ShouldIncludeBookingsButNoComments() {
        Collection<Booking> bookings = List.of(lastBooking, nextBooking);

        ItemDto result = ItemMapper.toItemDto(item, owner.getId(), bookings, null);

        assertNotNull(result);

        assertNotNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());

        assertNotNull(result.getComments());
        assertTrue(result.getComments().isEmpty());
    }

    @Test
    void toItemDto_ShouldOrderBookingsCorrectly_WhenMultipleBookingsExist() {
        Booking olderPastBooking = new Booking();
        olderPastBooking.setId(6L);
        olderPastBooking.setStart(now.minusDays(20));
        olderPastBooking.setEnd(now.minusDays(15));
        olderPastBooking.setItem(item);
        olderPastBooking.setBooker(booker);
        olderPastBooking.setStatus(BookingStatus.APPROVED);

        Booking closerPastBooking = new Booking();
        closerPastBooking.setId(7L);
        closerPastBooking.setStart(now.minusDays(3));
        closerPastBooking.setEnd(now.minusDays(2));
        closerPastBooking.setItem(item);
        closerPastBooking.setBooker(booker);
        closerPastBooking.setStatus(BookingStatus.APPROVED);

        Booking closerFutureBooking = new Booking();
        closerFutureBooking.setId(8L);
        closerFutureBooking.setStart(now.plusDays(1));
        closerFutureBooking.setEnd(now.plusDays(2));
        closerFutureBooking.setItem(item);
        closerFutureBooking.setBooker(booker);
        closerFutureBooking.setStatus(BookingStatus.APPROVED);

        Booking furtherFutureBooking = new Booking();
        furtherFutureBooking.setId(9L);
        furtherFutureBooking.setStart(now.plusDays(10));
        furtherFutureBooking.setEnd(now.plusDays(12));
        furtherFutureBooking.setItem(item);
        furtherFutureBooking.setBooker(booker);
        furtherFutureBooking.setStatus(BookingStatus.APPROVED);

        Collection<Booking> bookings = List.of(
                olderPastBooking, closerPastBooking,
                closerFutureBooking, furtherFutureBooking
        );

        ItemDto result = ItemMapper.toItemDto(item, owner.getId(), bookings, List.of());

        assertNotNull(result);

        BookingShort lastBookingShort = result.getLastBooking();
        assertNotNull(lastBookingShort);
        assertEquals(closerPastBooking.getId(), lastBookingShort.getId());

        BookingShort nextBookingShort = result.getNextBooking();
        assertNotNull(nextBookingShort);
        assertEquals(closerFutureBooking.getId(), nextBookingShort.getId());
    }
}