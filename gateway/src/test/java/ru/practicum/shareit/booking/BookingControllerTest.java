package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;

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
    private BookingClient bookingClient;

    private BookItemRequestDto bookItemRequestDto;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now().withNano(0);

        bookItemRequestDto = new BookItemRequestDto(
                1L,
                now.plusDays(1),
                now.plusDays(2)
        );
    }

    @Test
    void getBookings_ShouldReturnBadRequest_WhenHeaderIsMissing() throws Exception {
        mockMvc.perform(get("/bookings"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookItem_ShouldCreateBooking_WhenValidRequest() throws Exception {
        when(bookingClient.bookItem(eq(1L), any(BookItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/bookings")
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookItemRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void bookItem_ShouldReturnBadRequest_WhenStartIsInPast() throws Exception {
        bookItemRequestDto = new BookItemRequestDto(
                1L,
                now.minusDays(1),
                now.plusDays(2)
        );

        mockMvc.perform(post("/bookings")
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookItemRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookItem_ShouldReturnBadRequest_WhenHeaderIsMissing() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookItemRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBooking_ShouldReturnBooking_WhenValidRequest() throws Exception {
        when(bookingClient.getBooking(eq(1L), eq(1L)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getBooking_ShouldReturnBadRequest_WhenBookingIdIsInvalid() throws Exception {
        mockMvc.perform(get("/bookings/{bookingId}", "invalid")
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOwnerBookings_ShouldReturnOwnerBookings_WithDefaultParams() throws Exception {
        when(bookingClient.getBookingsByOwner(eq(1L), eq(BookingState.ALL), eq(0), eq(10)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/owner")
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getOwnerBookings_ShouldReturnOwnerBookings_WithCustomParams() throws Exception {
        when(bookingClient.getBookingsByOwner(eq(1L), eq(BookingState.PAST), eq(2), eq(15)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/owner")
                        .header(X_SHARER_USER_ID, "1")
                        .param("state", "PAST")
                        .param("from", "2")
                        .param("size", "15"))
                .andExpect(status().isOk());
    }

    @Test
    void updateBooking_ShouldUpdateBooking_WhenApprovedIsTrue() throws Exception {
        when(bookingClient.updateBooking(eq(1L), eq(1L), eq(true)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header(X_SHARER_USER_ID, "1")
                        .param("approved", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void updateBooking_ShouldUpdateBooking_WhenApprovedIsFalse() throws Exception {
        when(bookingClient.updateBooking(eq(1L), eq(1L), eq(false)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header(X_SHARER_USER_ID, "1")
                        .param("approved", "false"))
                .andExpect(status().isOk());
    }

    @Test
    void updateBooking_ShouldReturnBadRequest_WhenBookingIdIsInvalid() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", "invalid")
                        .header(X_SHARER_USER_ID, "1")
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBooking_ShouldReturnBadRequest_WhenApprovedParamIsMissing() throws Exception {
        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isBadRequest());
    }
}
