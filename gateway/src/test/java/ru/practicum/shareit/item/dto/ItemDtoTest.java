package ru.practicum.shareit.item.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ItemDtoTest {

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
    void shouldCreateItemDtoUsingBuilder() {
        CommentDto comment = CommentDto.builder()
                .id(1L)
                .text("test-comment-text-1")
                .authorName("test-author-name-1")
                .created(now)
                .build();

        ItemDto.BookingShort lastBooking = ItemDto.BookingShort.builder()
                .id(1L)
                .bookerId(2L)
                .start(now.minusDays(2))
                .end(now.minusDays(1))
                .build();

        ItemDto.BookingShort nextBooking = ItemDto.BookingShort.builder()
                .id(2L)
                .bookerId(3L)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();

        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("test-item-name-1")
                .description("test-item-description-1")
                .available(true)
                .comments(List.of(comment))
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .requestId(1L)
                .build();

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("test-item-name-1", dto.getName());
        assertEquals("test-item-description-1", dto.getDescription());
        assertTrue(dto.getAvailable());
        assertEquals(1, dto.getComments().size());
        assertEquals(comment, dto.getComments().iterator().next());
        assertEquals(lastBooking, dto.getLastBooking());
        assertEquals(nextBooking, dto.getNextBooking());
        assertEquals(1L, dto.getRequestId());
    }

    @Test
    void shouldCreateItemDtoUsingToBuilder() {
        ItemDto original = ItemDto.builder()
                .id(1L)
                .name("test-item-name-1")
                .description("test-item-description-1")
                .available(true)
                .build();

        ItemDto modified = original.toBuilder()
                .name("test-item-name-updated")
                .available(false)
                .build();

        assertEquals(1L, modified.getId());
        assertEquals("test-item-name-updated", modified.getName());
        assertEquals("test-item-description-1", modified.getDescription());
        assertFalse(modified.getAvailable());
    }

    @Test
    void shouldCreateEmptyItemDto() {
        ItemDto dto = ItemDto.builder().build();

        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getDescription());
        assertNull(dto.getAvailable());
        assertNull(dto.getComments());
        assertNull(dto.getLastBooking());
        assertNull(dto.getNextBooking());
        assertNull(dto.getRequestId());
    }

    @Test
    void shouldSetAndGetFields() {
        ItemDto dto = ItemDto.builder().build();

        dto.setId(1L);
        dto.setName("test-item-name-1");
        dto.setDescription("test-item-description-1");
        dto.setAvailable(true);

        Collection<CommentDto> comments = List.of(new CommentDto(1L, "test", "author", now));
        dto.setComments(comments);

        ItemDto.BookingShort lastBooking = ItemDto.BookingShort.builder()
                .id(1L)
                .bookerId(2L)
                .start(now.minusDays(2))
                .end(now.minusDays(1))
                .build();
        dto.setLastBooking(lastBooking);

        ItemDto.BookingShort nextBooking = ItemDto.BookingShort.builder()
                .id(2L)
                .bookerId(3L)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();
        dto.setNextBooking(nextBooking);

        dto.setRequestId(1L);

        assertEquals(1L, dto.getId());
        assertEquals("test-item-name-1", dto.getName());
        assertEquals("test-item-description-1", dto.getDescription());
        assertTrue(dto.getAvailable());
        assertEquals(comments, dto.getComments());
        assertEquals(lastBooking, dto.getLastBooking());
        assertEquals(nextBooking, dto.getNextBooking());
        assertEquals(1L, dto.getRequestId());
    }

    @Test
    void shouldFailValidationWhenNameIsNull() {
        ItemDto dto = ItemDto.builder()
                .name(null)
                .description("test-item-description-1")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Название не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenNameIsBlank() {
        ItemDto dto = ItemDto.builder()
                .name("   ")
                .description("test-item-description-1")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Название не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenNameIsEmpty() {
        ItemDto dto = ItemDto.builder()
                .name("")
                .description("test-item-description-1")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Название не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenDescriptionIsNull() {
        ItemDto dto = ItemDto.builder()
                .name("test-item-name-1")
                .description(null)
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Описание не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenDescriptionIsBlank() {
        ItemDto dto = ItemDto.builder()
                .name("test-item-name-1")
                .description("   ")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Описание не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenDescriptionIsEmpty() {
        ItemDto dto = ItemDto.builder()
                .name("test-item-name-1")
                .description("")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Описание не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenAvailableIsNull() {
        ItemDto dto = ItemDto.builder()
                .name("test-item-name-1")
                .description("test-item-description-1")
                .available(null)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Статус должен быть указан", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWithMultipleViolations() {
        ItemDto dto = ItemDto.builder()
                .name(null)
                .description(null)
                .available(null)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(3, violations.size());

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Название не может быть пустым")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Описание не может быть пустым")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Статус должен быть указан")));
    }

    @Test
    void shouldPassValidationWhenAllFieldsAreValid() {
        ItemDto dto = ItemDto.builder()
                .name("test-item-name-1")
                .description("test-item-description-1")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldAllowIdToBeNull() {
        ItemDto dto = ItemDto.builder()
                .id(null)
                .name("test-item-name-1")
                .description("test-item-description-1")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldAllowCommentsToBeNull() {
        ItemDto dto = ItemDto.builder()
                .name("test-item-name-1")
                .description("test-item-description-1")
                .available(true)
                .comments(null)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldAllowLastBookingToBeNull() {
        ItemDto dto = ItemDto.builder()
                .name("test-item-name-1")
                .description("test-item-description-1")
                .available(true)
                .lastBooking(null)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldAllowNextBookingToBeNull() {
        ItemDto dto = ItemDto.builder()
                .name("test-item-name-1")
                .description("test-item-description-1")
                .available(true)
                .nextBooking(null)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldAllowRequestIdToBeNull() {
        ItemDto dto = ItemDto.builder()
                .name("test-item-name-1")
                .description("test-item-description-1")
                .available(true)
                .requestId(null)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testEqualsAndHashCode() {
        ItemDto dto1 = ItemDto.builder()
                .id(1L)
                .name("test-name-1")
                .description("test-desc-1")
                .available(true)
                .build();

        ItemDto dto2 = ItemDto.builder()
                .id(1L)
                .name("test-name-1")
                .description("test-desc-1")
                .available(true)
                .build();

        ItemDto dto3 = ItemDto.builder()
                .id(2L)
                .name("test-name-2")
                .description("test-desc-2")
                .available(false)
                .build();

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testEqualsWithSameObject() {
        ItemDto dto = ItemDto.builder().id(1L).build();

        assertEquals(dto, dto);
    }

    @Test
    void testEqualsWithNull() {
        ItemDto dto = ItemDto.builder().id(1L).build();

        assertNotEquals(null, dto);
    }

    @Test
    void testEqualsWithDifferentClass() {
        ItemDto dto = ItemDto.builder().id(1L).build();

        assertNotEquals("string", dto);
    }

    @Test
    void testToString() {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("test-name-1")
                .description("test-desc-1")
                .available(true)
                .build();

        String toString = dto.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("test-name-1"));
        assertTrue(toString.contains("test-desc-1"));
        assertTrue(toString.contains("true"));
    }

    // Тесты для вложенного класса BookingShort
    @Test
    void shouldCreateBookingShortUsingBuilder() {
        ItemDto.BookingShort bookingShort = ItemDto.BookingShort.builder()
                .id(1L)
                .bookerId(2L)
                .start(now)
                .end(now.plusDays(1))
                .build();

        assertNotNull(bookingShort);
        assertEquals(1L, bookingShort.getId());
        assertEquals(2L, bookingShort.getBookerId());
        assertEquals(now, bookingShort.getStart());
        assertEquals(now.plusDays(1), bookingShort.getEnd());
    }

    @Test
    void shouldCreateBookingShortUsingConstructor() {
        ItemDto.BookingShort bookingShort = new ItemDto.BookingShort(
                1L, 2L, now, now.plusDays(1)
        );

        assertNotNull(bookingShort);
        assertEquals(1L, bookingShort.getId());
        assertEquals(2L, bookingShort.getBookerId());
        assertEquals(now, bookingShort.getStart());
        assertEquals(now.plusDays(1), bookingShort.getEnd());
    }

    @Test
    void shouldCreateEmptyBookingShortUsingNoArgsConstructor() {
        ItemDto.BookingShort bookingShort = new ItemDto.BookingShort();

        assertNotNull(bookingShort);
        assertNull(bookingShort.getId());
        assertNull(bookingShort.getBookerId());
        assertNull(bookingShort.getStart());
        assertNull(bookingShort.getEnd());
    }

    @Test
    void shouldSetAndGetBookingShortFields() {
        ItemDto.BookingShort bookingShort = new ItemDto.BookingShort();
        bookingShort.setId(1L);
        bookingShort.setBookerId(2L);
        bookingShort.setStart(now);
        bookingShort.setEnd(now.plusDays(1));

        assertEquals(1L, bookingShort.getId());
        assertEquals(2L, bookingShort.getBookerId());
        assertEquals(now, bookingShort.getStart());
        assertEquals(now.plusDays(1), bookingShort.getEnd());
    }
}