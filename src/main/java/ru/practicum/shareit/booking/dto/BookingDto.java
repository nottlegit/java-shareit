package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;

    @NotNull(message = "Время начала бронирования должно быть указано")
    @FutureOrPresent(message = "Время начала бронирования не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "Время окончания бронирования должно быть указано")
    @Future(message = "Время окончания бронирования должно быть в будущем")
    private LocalDateTime end;

    @NotNull(message = "ID вещи должно быть указано")
    private Long itemId;
}
