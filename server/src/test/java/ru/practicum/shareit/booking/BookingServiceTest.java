package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @PersistenceContext
    private EntityManager entityManager;

    private UserDto ownerDto;
    private UserDto bookerDto;
    private UserDto anotherUserDto;
    private ItemDto itemDto;
    private ItemDto unavailableItemDto;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now().withNano(0);

        ownerDto = UserDto.builder()
                .name("Owner")
                .email("owner@test.com")
                .build();
        ownerDto = userService.createUser(ownerDto);

        bookerDto = UserDto.builder()
                .name("Booker")
                .email("booker@test.com")
                .build();
        bookerDto = userService.createUser(bookerDto);

        anotherUserDto = UserDto.builder()
                .name("Another")
                .email("another@test.com")
                .build();
        anotherUserDto = userService.createUser(anotherUserDto);

        itemDto = ItemDto.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .build();
        itemDto = itemService.createItem(ownerDto.getId(), itemDto);

        unavailableItemDto = ItemDto.builder()
                .name("Broken Drill")
                .description("Not working")
                .available(false)
                .build();
        unavailableItemDto = itemService.createItem(ownerDto.getId(), unavailableItemDto);

        entityManager.flush();
    }

    @Test
    void createBooking_ShouldCreateBooking_WhenValidRequest() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDto.getId());
        bookingDto.setStart(now.plusDays(1));
        bookingDto.setEnd(now.plusDays(2));

        BookingResponseDto response = bookingService.createBooking(bookingDto, bookerDto.getId());

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(itemDto.getId(), response.getItem().getId());
        assertEquals(bookerDto.getId(), response.getBooker().getId());
        assertEquals(BookingStatus.WAITING, response.getStatus());
        assertEquals(bookingDto.getStart(), response.getStart());
        assertEquals(bookingDto.getEnd(), response.getEnd());
    }

    @Test
    void createBooking_ShouldThrowException_WhenItemNotAvailable() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(unavailableItemDto.getId());
        bookingDto.setStart(now.plusDays(1));
        bookingDto.setEnd(now.plusDays(2));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(bookingDto, bookerDto.getId()));
        assertEquals("Вещь недоступна для бронирования", exception.getMessage());
    }

    @Test
    void createBooking_ShouldThrowException_WhenUserIsOwner() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDto.getId());
        bookingDto.setStart(now.plusDays(1));
        bookingDto.setEnd(now.plusDays(2));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(bookingDto, ownerDto.getId()));
        assertEquals("Нельзя забронировать свою вещь", exception.getMessage());
    }

    @Test
    void updateBookingStatus_ShouldApproveBooking_WhenOwnerApproves() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDto.getId());
        bookingDto.setStart(now.plusDays(1));
        bookingDto.setEnd(now.plusDays(2));
        BookingResponseDto createdBooking = bookingService.createBooking(bookingDto, bookerDto.getId());

        BookingResponseDto updatedBooking = bookingService.updateBookingStatus(
                createdBooking.getId(), true, ownerDto.getId());

        assertNotNull(updatedBooking);
        assertEquals(createdBooking.getId(), updatedBooking.getId());
        assertEquals(BookingStatus.APPROVED, updatedBooking.getStatus());
    }

    @Test
    void updateBookingStatus_ShouldRejectBooking_WhenOwnerRejects() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDto.getId());
        bookingDto.setStart(now.plusDays(1));
        bookingDto.setEnd(now.plusDays(2));
        BookingResponseDto createdBooking = bookingService.createBooking(bookingDto, bookerDto.getId());

        BookingResponseDto updatedBooking = bookingService.updateBookingStatus(
                createdBooking.getId(), false, ownerDto.getId());

        assertNotNull(updatedBooking);
        assertEquals(createdBooking.getId(), updatedBooking.getId());
        assertEquals(BookingStatus.REJECTED, updatedBooking.getStatus());
    }

    @Test
    void updateBookingStatus_ShouldThrowException_WhenNotOwner() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDto.getId());
        bookingDto.setStart(now.plusDays(1));
        bookingDto.setEnd(now.plusDays(2));
        BookingResponseDto createdBooking = bookingService.createBooking(bookingDto, bookerDto.getId());

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> bookingService.updateBookingStatus(createdBooking.getId(), true, anotherUserDto.getId()));
        assertEquals("Только владелец может подтвердить бронирование", exception.getMessage());
    }

    @Test
    void updateBookingStatus_ShouldThrowException_WhenBookingNotWaiting() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDto.getId());
        bookingDto.setStart(now.plusDays(1));
        bookingDto.setEnd(now.plusDays(2));
        BookingResponseDto createdBooking = bookingService.createBooking(bookingDto, bookerDto.getId());

        bookingService.updateBookingStatus(createdBooking.getId(), true, ownerDto.getId());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bookingService.updateBookingStatus(createdBooking.getId(), false, ownerDto.getId()));
        assertEquals("Можно подтверждать только ожидающие запросы", exception.getMessage());
    }

    @Test
    void getBookingById_ShouldReturnBooking_WhenUserIsOwner() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDto.getId());
        bookingDto.setStart(now.plusDays(1));
        bookingDto.setEnd(now.plusDays(2));
        BookingResponseDto createdBooking = bookingService.createBooking(bookingDto, bookerDto.getId());

        BookingResponseDto foundBooking = bookingService.getBookingById(createdBooking.getId(), ownerDto.getId());

        assertNotNull(foundBooking);
        assertEquals(createdBooking.getId(), foundBooking.getId());
    }

    @Test
    void getBookingById_ShouldReturnBooking_WhenUserIsBooker() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDto.getId());
        bookingDto.setStart(now.plusDays(1));
        bookingDto.setEnd(now.plusDays(2));
        BookingResponseDto createdBooking = bookingService.createBooking(bookingDto, bookerDto.getId());

        BookingResponseDto foundBooking = bookingService.getBookingById(createdBooking.getId(), bookerDto.getId());

        assertNotNull(foundBooking);
        assertEquals(createdBooking.getId(), foundBooking.getId());
    }

    @Test
    void getBookingById_ShouldThrowException_WhenUserHasNoAccess() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemDto.getId());
        bookingDto.setStart(now.plusDays(1));
        bookingDto.setEnd(now.plusDays(2));
        BookingResponseDto createdBooking = bookingService.createBooking(bookingDto, bookerDto.getId());

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> bookingService.getBookingById(createdBooking.getId(), anotherUserDto.getId()));
        assertEquals("Доступ запрещён", exception.getMessage());
    }

    @Test
    void getBookingById_ShouldThrowException_WhenBookingNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(999L, bookerDto.getId()));
        assertEquals("Бронирование не найдено", exception.getMessage());
    }

    @Test
    void getBookingsByUser_ShouldReturnAllBookings_WhenStateIsAll() {
        createTestBookings();

        Collection<BookingResponseDto> bookings = bookingService.getBookingsByUser(
                bookerDto.getId(), "ALL", 0, 10);

        assertNotNull(bookings);
        assertEquals(4, bookings.size());
    }

    @Test
    void getBookingsByUser_ShouldReturnCurrentBookings_WhenStateIsCurrent() {
        createTestBookings();

        Collection<BookingResponseDto> bookings = bookingService.getBookingsByUser(
                bookerDto.getId(), "CURRENT", 0, 10);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    void getBookingsByUser_ShouldReturnPastBookings_WhenStateIsPast() {
        createTestBookings();

        Collection<BookingResponseDto> bookings = bookingService.getBookingsByUser(
                bookerDto.getId(), "PAST", 0, 10);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    void getBookingsByUser_ShouldReturnRejectedBookings_WhenStateIsRejected() {
        createTestBookings();

        Collection<BookingResponseDto> bookings = bookingService.getBookingsByUser(
                bookerDto.getId(), "REJECTED", 0, 10);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(BookingStatus.REJECTED, bookings.iterator().next().getStatus());
    }

    @Test
    void getBookingsByUser_ShouldReturnEmptyList_WhenNoBookings() {
        Collection<BookingResponseDto> bookings = bookingService.getBookingsByUser(
                bookerDto.getId(), "ALL", 0, 10);

        assertNotNull(bookings);
        assertTrue(bookings.isEmpty());
    }

    @Test
    void getBookingsByOwner_ShouldReturnAllBookings_WhenStateIsAll() {
        createTestBookings();

        Collection<BookingResponseDto> bookings = bookingService.getBookingsByOwner(
                ownerDto.getId(), "ALL", 0, 10);

        assertNotNull(bookings);
        assertEquals(4, bookings.size());
    }

    @Test
    void getBookingsByOwner_ShouldReturnCurrentBookings_WhenStateIsCurrent() {
        createTestBookings();

        Collection<BookingResponseDto> bookings = bookingService.getBookingsByOwner(
                ownerDto.getId(), "CURRENT", 0, 10);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    void getBookingsByOwner_ShouldReturnPastBookings_WhenStateIsPast() {
        createTestBookings();

        Collection<BookingResponseDto> bookings = bookingService.getBookingsByOwner(
                ownerDto.getId(), "PAST", 0, 10);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
    }

    @Test
    void getBookingsByOwner_ShouldReturnRejectedBookings_WhenStateIsRejected() {
        createTestBookings();

        Collection<BookingResponseDto> bookings = bookingService.getBookingsByOwner(
                ownerDto.getId(), "REJECTED", 0, 10);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(BookingStatus.REJECTED, bookings.iterator().next().getStatus());
    }

    @Test
    void getBookingsByOwner_ShouldReturnEmptyList_WhenOwnerHasNoItems() {
        UserDto newUserDto = UserDto.builder()
                .name("New User")
                .email("newuser@test.com")
                .build();
        newUserDto = userService.createUser(newUserDto);

        Collection<BookingResponseDto> bookings = bookingService.getBookingsByOwner(
                newUserDto.getId(), "ALL", 0, 10);

        assertNotNull(bookings);
        assertTrue(bookings.isEmpty());
    }

    @Test
    void getPageable_ShouldCreateCorrectPageRequest() {
        createTestBookings();

        Collection<BookingResponseDto> firstPage = bookingService.getBookingsByUser(
                bookerDto.getId(), "ALL", 0, 2);
        Collection<BookingResponseDto> secondPage = bookingService.getBookingsByUser(
                bookerDto.getId(), "ALL", 2, 2);

        assertEquals(2, firstPage.size());
        assertEquals(2, secondPage.size());
    }

    private void createTestBookings() {
        BookingDto pastBooking = new BookingDto();
        pastBooking.setItemId(itemDto.getId());
        pastBooking.setStart(now.minusDays(2));
        pastBooking.setEnd(now.minusDays(1));
        bookingService.createBooking(pastBooking, bookerDto.getId());

        BookingDto currentBooking = new BookingDto();
        currentBooking.setItemId(itemDto.getId());
        currentBooking.setStart(now.minusHours(1));
        currentBooking.setEnd(now.plusHours(1));
        bookingService.createBooking(currentBooking, bookerDto.getId());

        BookingDto futureBooking = new BookingDto();
        futureBooking.setItemId(itemDto.getId());
        futureBooking.setStart(now.plusDays(1));
        futureBooking.setEnd(now.plusDays(2));
        bookingService.createBooking(futureBooking, bookerDto.getId());

        BookingDto rejectedBooking = new BookingDto();
        rejectedBooking.setItemId(itemDto.getId());
        rejectedBooking.setStart(now.plusDays(3));
        rejectedBooking.setEnd(now.plusDays(4));
        BookingResponseDto rejected = bookingService.createBooking(rejectedBooking, bookerDto.getId());
        bookingService.updateBookingStatus(rejected.getId(), false, ownerDto.getId());

        entityManager.flush();
    }
}