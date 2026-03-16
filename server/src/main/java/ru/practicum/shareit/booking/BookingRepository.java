package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long bookerId, LocalDateTime cutOffStart, LocalDateTime cutOffEnd, Pageable pageable);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(
            Long bookerId, LocalDateTime thresholdDateTime, Pageable pageable);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(
            Long bookerId, LocalDateTime thresholdDateTime, Pageable pageable);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(
            Long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(
            List<Long> itemIds, LocalDateTime cutOffStart, LocalDateTime cutOffEnd);

    List<Booking> findByItemIdInAndEndBeforeOrderByStartDesc(
            List<Long> itemIds, LocalDateTime cutOffDateTime);

    List<Booking> findByItemIdInAndStartAfterOrderByStartDesc(
            List<Long> itemIds, LocalDateTime thresholdDateTime);

    List<Booking> findByItemIdInAndStatusOrderByStartDesc(
            List<Long> itemIds, BookingStatus status);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

    Boolean existsByBookerIdAndItemIdAndEndBefore(Long userId, Long itemId, LocalDateTime localDateTime);

    List<Booking> findByItemIdAndStatusNotOrderByStart(Long itemId, BookingStatus status);
}
