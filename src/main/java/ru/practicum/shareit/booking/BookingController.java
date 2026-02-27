package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final String RESPONSE_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(
            @RequestHeader(RESPONSE_HEADER) Long userId,
            @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(
            @PathVariable Long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader(RESPONSE_HEADER) Long ownerId) {
        return bookingService.approveBooking(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(
            @PathVariable Long bookingId,
            @RequestHeader(RESPONSE_HEADER) Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }


}

