package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private BookingDto bookingDto;
    private BookingResponseDto bookingResponseDto;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now().withNano(0);

        bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(now.plusDays(1));
        bookingDto.setEnd(now.plusDays(2));

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("test-item-name")
                .description("test-item-description")
                .available(true)
                .build();

        UserDto bookerDto = UserDto.builder()
                .id(2L)
                .name("test-booker-name")
                .email("test-booker@test.com")
                .build();

        bookingResponseDto = BookingResponseDto.builder()
                .id(1L)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(BookingStatus.WAITING)
                .item(itemDto)
                .booker(bookerDto)
                .build();
    }

    @Test
    void createBooking_ShouldReturnBooking_WhenValidRequest() throws Exception {
        when(bookingService.createBooking(any(BookingDto.class), eq(1L)))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(post("/bookings")
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("test-item-name"))
                .andExpect(jsonPath("$.booker.id").value(2))
                .andExpect(jsonPath("$.booker.name").value("test-booker-name"));
    }

    @Test
    void updateBookingStatus_ShouldReturnApprovedBooking_WhenApprovedIsTrue() throws Exception {
        BookingResponseDto approvedBooking = BookingResponseDto.builder()
                .id(bookingResponseDto.getId())
                .start(bookingResponseDto.getStart())
                .end(bookingResponseDto.getEnd())
                .status(BookingStatus.APPROVED)
                .item(bookingResponseDto.getItem())
                .booker(bookingResponseDto.getBooker())
                .build();

        when(bookingService.updateBookingStatus(eq(1L), eq(true), eq(1L)))
                .thenReturn(approvedBooking);

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header(X_SHARER_USER_ID, "1")
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void updateBookingStatus_ShouldReturnRejectedBooking_WhenApprovedIsFalse() throws Exception {
        BookingResponseDto rejectedBooking = BookingResponseDto.builder()
                .id(bookingResponseDto.getId())
                .start(bookingResponseDto.getStart())
                .end(bookingResponseDto.getEnd())
                .status(BookingStatus.REJECTED)
                .item(bookingResponseDto.getItem())
                .booker(bookingResponseDto.getBooker())
                .build();

        when(bookingService.updateBookingStatus(eq(1L), eq(false), eq(1L)))
                .thenReturn(rejectedBooking);

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header(X_SHARER_USER_ID, "1")
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void getBookingById_ShouldReturnBooking_WhenUserIsAuthorized() throws Exception {
        when(bookingService.getBookingById(eq(1L), eq(1L)))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void getBookingsByUser_ShouldReturnListOfBookings_WithDefaultParams() throws Exception {
        when(bookingService.getBookingsByUser(eq(1L), eq("ALL"), eq(0), eq(10)))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings")
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getBookingsByUser_ShouldReturnListOfBookings_WithCustomParams() throws Exception {
        when(bookingService.getBookingsByUser(eq(1L), eq("CURRENT"), eq(5), eq(20)))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings")
                        .header(X_SHARER_USER_ID, "1")
                        .param("state", "CURRENT")
                        .param("from", "5")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getBookingsByUser_ShouldReturnBadRequest_WhenFromIsNegative() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(X_SHARER_USER_ID, "1")
                        .param("from", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsByUser_ShouldReturnBadRequest_WhenSizeIsZero() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(X_SHARER_USER_ID, "1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsByUser_ShouldReturnBadRequest_WhenSizeIsNegative() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(X_SHARER_USER_ID, "1")
                        .param("size", "-5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsByOwner_ShouldReturnListOfBookings_WithDefaultParams() throws Exception {
        when(bookingService.getBookingsByOwner(eq(1L), eq("ALL"), eq(0), eq(10)))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings/owner")
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getBookingsByOwner_ShouldReturnListOfBookings_WithCustomParams() throws Exception {
        when(bookingService.getBookingsByOwner(eq(1L), eq("PAST"), eq(2), eq(15)))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings/owner")
                        .header(X_SHARER_USER_ID, "1")
                        .param("state", "PAST")
                        .param("from", "2")
                        .param("size", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getBookingsByOwner_ShouldReturnBadRequest_WhenFromIsNegative() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header(X_SHARER_USER_ID, "1")
                        .param("from", "-10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsByOwner_ShouldReturnBadRequest_WhenSizeIsZero() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header(X_SHARER_USER_ID, "1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkParametersPagination_ShouldThrowValidationException_WhenInvalidParams() throws Exception {
        when(bookingService.getBookingsByUser(eq(1L), eq("ALL"), eq(-1), eq(10)))
                .thenThrow(new ValidationException("Параметры 'from' и 'size' должны быть положительными"));

        mockMvc.perform(get("/bookings")
                        .header(X_SHARER_USER_ID, "1")
                        .param("from", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsByUser_ShouldHandleEmptyList() throws Exception {
        when(bookingService.getBookingsByUser(eq(1L), eq("ALL"), eq(0), eq(10)))
                .thenReturn(List.of());

        mockMvc.perform(get("/bookings")
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getBookingsByOwner_ShouldHandleEmptyList() throws Exception {
        when(bookingService.getBookingsByOwner(eq(1L), eq("ALL"), eq(0), eq(10)))
                .thenReturn(List.of());

        mockMvc.perform(get("/bookings/owner")
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}