package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Transactional
    public BookingResponseDto createBooking(BookingDto bookingDto, Long userId) {
        User user = userService.findUserByIdOrThrow(userId);
        Item item = itemService.findItemByIdOrThrow(bookingDto.getItemId());

        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Вещь недоступна для бронирования");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Нельзя забронировать свою вещь");
        }

        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        booking.setStatus(BookingStatus.WAITING);

        booking = bookingRepository.save(booking);

        log.info("Вещь с id: {}. Успешно забронирована", item.getId());
        return BookingMapper.toResponseDto(booking);
    }

    @Transactional
    public BookingResponseDto updateBookingStatus(Long bookingId, Boolean approved, Long ownerId) {
        Booking booking = findBookingByIdOrThrow(bookingId);
        Item item = itemService.findItemByIdOrThrow(booking.getItem().getId());

        if (!item.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Только владелец может подтвердить бронирование");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new IllegalArgumentException("Можно подтверждать только ожидающие запросы");
        }


        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        Booking updated = bookingRepository.save(booking);
        return BookingMapper.toResponseDto(updated);
    }

    public BookingResponseDto getBookingById(Long bookingId, Long userId) {
        Booking booking = findBookingByIdOrThrow(bookingId);
        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();

        if (!ownerId.equals(userId) && !bookerId.equals(userId)) {
            throw new AccessDeniedException("Доступ запрещён");
        }

        return BookingMapper.toResponseDto(booking);
    }

    public Collection<BookingResponseDto> getBookingsByUser(Long userId, String state, Integer from, Integer size) {
        userService.findUserByIdOrThrow(userId);

        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = getPageable(from, size);

        Collection<Booking> bookings = switch (BookingState.valueOf(state)) {
            case CURRENT -> bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                    userId, now, now, pageable);
            case PAST -> bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(
                    userId, now, pageable);
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                    userId, now, pageable);
            case WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                    userId, BookingStatus.WAITING, pageable);
            case REJECTED -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                    userId, BookingStatus.REJECTED, pageable);
            default -> bookingRepository.findByBookerIdOrderByStartDesc(userId, pageable);
        };

        return BookingMapper.toResponseDtoCollection(bookings);
    }

    public Collection<BookingResponseDto> getBookingsByOwner(Long ownerId, String state, Integer from, Integer size) {
        userService.findUserByIdOrThrow(ownerId);

        Pageable pageable = getPageable(from, size);

        List<Long> itemsIds = itemService.findByOwnerId(ownerId).stream()
                .map(Item::getId)
                .toList();

        if (itemsIds.isEmpty()) {
            return List.of();
        }

        LocalDateTime now = LocalDateTime.now();

        Collection<Booking> bookings = switch (BookingState.valueOf(state)) {
            case CURRENT -> bookingRepository.findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(
                    itemsIds, now, now);
            case PAST -> bookingRepository.findByItemIdInAndEndBeforeOrderByStartDesc(itemsIds, now);
            case FUTURE -> bookingRepository.findByItemIdInAndStartAfterOrderByStartDesc(itemsIds, now);
            case WAITING -> bookingRepository.findByItemIdInAndStatusOrderByStartDesc(
                    itemsIds, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByItemIdInAndStatusOrderByStartDesc(
                    itemsIds, BookingStatus.REJECTED);
            default -> bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId, pageable);
        };

        return BookingMapper.toResponseDtoCollection(bookings);
    }

    private Booking findBookingByIdOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
    }

    private Pageable getPageable(Integer from, Integer size) {
        return PageRequest.of(from / size, size);
    }
}
