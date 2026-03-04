package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
 import ru.practicum.shareit.booking.dto.BookingResponseDto;
 import ru.practicum.shareit.exception.ValidationException;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final String xSharerUserId = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(
            @RequestHeader(xSharerUserId) Long userId,
            @RequestBody @Valid BookingDto bookingDto) {
        log.info("Получен запрос от пользователя: {}. На бронирование вещи: {}.", userId, bookingDto.getItemId());
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateBookingStatus(
            @PathVariable Long bookingId,
            @RequestParam Boolean approved,
            @RequestHeader(xSharerUserId) Long ownerId) {
        return bookingService.updateBookingStatus(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(
            @PathVariable Long bookingId,
            @RequestHeader(xSharerUserId) Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingResponseDto> getBookingsByUser(
            @RequestHeader(xSharerUserId) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {

        checkParametersPagination(from, size);

        return bookingService.getBookingsByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingResponseDto> getBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {

        checkParametersPagination(from, size);
        return bookingService.getBookingsByOwner(ownerId, state, from, size);
    }

    private void checkParametersPagination(Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Параметры 'from' и 'size' должны быть положительными");
        }
    }
}
