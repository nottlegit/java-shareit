package ru.practicum.shareit.item.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.comment.CommentDto;

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
                .text("test-comment-text")
                .authorName("test-author")
                .created(now)
                .build();

        BookingShort lastBooking = BookingShort.builder()
                .id(1L)
                .bookerId(2L)
                .start(now.minusDays(2))
                .end(now.minusDays(1))
                .build();

        BookingShort nextBooking = BookingShort.builder()
                .id(2L)
                .bookerId(3L)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();

        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("test-item-name")
                .description("test-item-description")
                .available(true)
                .comments(List.of(comment))
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .requestId(1L)
                .build();

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("test-item-name", dto.getName());
        assertEquals("test-item-description", dto.getDescription());
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
                .name("test-item-name")
                .description("test-item-description")
                .available(true)
                .build();

        ItemDto modified = original.toBuilder()
                .name("test-item-name-updated")
                .available(false)
                .build();

        assertEquals(1L, modified.getId());
        assertEquals("test-item-name-updated", modified.getName());
        assertEquals("test-item-description", modified.getDescription());
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
        dto.setName("test-item-name");
        dto.setDescription("test-item-description");
        dto.setAvailable(true);

        Collection<CommentDto> comments = List.of(new CommentDto());
        dto.setComments(comments);

        BookingShort lastBooking = new BookingShort();
        dto.setLastBooking(lastBooking);

        BookingShort nextBooking = new BookingShort();
        dto.setNextBooking(nextBooking);

        dto.setRequestId(1L);

        assertEquals(1L, dto.getId());
        assertEquals("test-item-name", dto.getName());
        assertEquals("test-item-description", dto.getDescription());
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
                .description("test-item-description")
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
                .description("test-item-description")
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
                .description("test-item-description")
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
                .name("test-item-name")
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
                .name("test-item-name")
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
                .name("test-item-name")
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
                .name("test-item-name")
                .description("test-item-description")
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
                .name("test-item-name")
                .description("test-item-description")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldAllowIdToBeNull() {
        ItemDto dto = ItemDto.builder()
                .id(null)
                .name("test-item-name")
                .description("test-item-description")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldAllowCommentsToBeNull() {
        ItemDto dto = ItemDto.builder()
                .name("test-item-name")
                .description("test-item-description")
                .available(true)
                .comments(null)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldAllowLastBookingToBeNull() {
        ItemDto dto = ItemDto.builder()
                .name("test-item-name")
                .description("test-item-description")
                .available(true)
                .lastBooking(null)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldAllowNextBookingToBeNull() {
        ItemDto dto = ItemDto.builder()
                .name("test-item-name")
                .description("test-item-description")
                .available(true)
                .nextBooking(null)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldAllowRequestIdToBeNull() {
        ItemDto dto = ItemDto.builder()
                .name("test-item-name")
                .description("test-item-description")
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
                .name("test-name")
                .description("test-desc")
                .available(true)
                .build();

        ItemDto dto2 = ItemDto.builder()
                .id(1L)
                .name("test-name")
                .description("test-desc")
                .available(true)
                .build();

        ItemDto dto3 = ItemDto.builder()
                .id(2L)
                .name("other-name")
                .description("other-desc")
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
                .name("test-name")
                .description("test-desc")
                .available(true)
                .build();

        String toString = dto.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("test-name"));
        assertTrue(toString.contains("test-desc"));
        assertTrue(toString.contains("true"));
    }
}