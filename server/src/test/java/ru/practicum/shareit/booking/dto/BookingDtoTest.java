package ru.practicum.shareit.booking.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BookingDtoTest {

    private Validator validator;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        now = LocalDateTime.now().withNano(0);
    }

    @Test
    void shouldCreateBookingDtoWithAllFields() {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(now.plusDays(1));
        dto.setEnd(now.plusDays(2));
        dto.setItemId(1L);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(now.plusDays(1), dto.getStart());
        assertEquals(now.plusDays(2), dto.getEnd());
        assertEquals(1L, dto.getItemId());
    }

    @Test
    void shouldCreateBookingDtoUsingConstructor() {
        BookingDto dto = new BookingDto(1L, now.plusDays(1), now.plusDays(2), 1L);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(now.plusDays(1), dto.getStart());
        assertEquals(now.plusDays(2), dto.getEnd());
        assertEquals(1L, dto.getItemId());
    }

    @Test
    void shouldFailValidationWhenStartIsNull() {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(null);
        dto.setEnd(now.plusDays(2));
        dto.setItemId(1L);

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Время начала бронирования должно быть указано", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenStartIsInPast() {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(now.minusDays(1));
        dto.setEnd(now.plusDays(2));
        dto.setItemId(1L);

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Время начала бронирования не может быть в прошлом", violations.iterator().next().getMessage());
    }

    @Test
    void shouldPassValidationWhenStartIsFuture() {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(now.plusDays(1));
        dto.setEnd(now.plusDays(2));
        dto.setItemId(1L);

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailValidationWhenEndIsNull() {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(now.plusDays(1));
        dto.setEnd(null);
        dto.setItemId(1L);

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Время окончания бронирования должно быть указано", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenEndIsPast() {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(now.minusDays(2));
        dto.setEnd(now.minusDays(1));
        dto.setItemId(1L);

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());

        boolean hasEndInPastViolation = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Время окончания бронирования должно быть в будущем"));
        assertTrue(hasEndInPastViolation);
    }

    @Test
    void shouldFailValidationWhenEndIsNotFuture() {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(now.plusDays(1));
        dto.setEnd(now);
        dto.setItemId(1L);

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Время окончания бронирования должно быть в будущем", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenItemIdIsNull() {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(now.plusDays(1));
        dto.setEnd(now.plusDays(2));
        dto.setItemId(null);

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("ID вещи должно быть указано", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWithMultipleViolations() {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(null);
        dto.setEnd(now.minusDays(1));
        dto.setItemId(null);

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(3, violations.size());

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Время начала бронирования должно быть указано")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Время окончания бронирования должно быть в будущем")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("ID вещи должно быть указано")));
    }

    @Test
    void shouldPassValidationWhenAllFieldsAreValid() {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(now.plusDays(1));
        dto.setEnd(now.plusDays(2));
        dto.setItemId(1L);

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldAllowIdToBeNull() {
        BookingDto dto = new BookingDto();
        dto.setId(null);
        dto.setStart(now.plusDays(1));
        dto.setEnd(now.plusDays(2));
        dto.setItemId(1L);

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}