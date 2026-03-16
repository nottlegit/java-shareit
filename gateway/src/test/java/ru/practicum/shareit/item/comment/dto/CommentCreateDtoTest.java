package ru.practicum.shareit.item.comment.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CommentCreateDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void shouldCreateCommentCreateDtoUsingConstructor() {
        CommentCreateDto dto = new CommentCreateDto("test-comment-text-1");

        assertNotNull(dto);
        assertEquals("test-comment-text-1", dto.getText());
    }

    @Test
    void shouldCreateEmptyCommentCreateDtoUsingNoArgsConstructor() {
        CommentCreateDto dto = new CommentCreateDto();

        assertNotNull(dto);
        assertNull(dto.getText());
    }

    @Test
    void shouldSetAndGetText() {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("test-comment-text-1");

        assertEquals("test-comment-text-1", dto.getText());
    }

    @Test
    void shouldFailValidationWhenTextIsNull() {
        CommentCreateDto dto = new CommentCreateDto(null);

        Set<ConstraintViolation<CommentCreateDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Текст комментария не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenTextIsEmpty() {
        CommentCreateDto dto = new CommentCreateDto("");

        Set<ConstraintViolation<CommentCreateDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Текст комментария не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenTextIsBlank() {
        CommentCreateDto dto = new CommentCreateDto("   ");

        Set<ConstraintViolation<CommentCreateDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Текст комментария не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void shouldPassValidationWhenTextIsNotEmpty() {
        CommentCreateDto dto = new CommentCreateDto("test-comment-text-1");

        Set<ConstraintViolation<CommentCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldPassValidationWhenTextHasLeadingAndTrailingSpaces() {
        CommentCreateDto dto = new CommentCreateDto("  test-comment-text-1  ");

        Set<ConstraintViolation<CommentCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testEqualsWithSameObject() {
        CommentCreateDto dto = new CommentCreateDto("test-comment-text-1");

        assertEquals(dto, dto);
    }

    @Test
    void testEqualsWithNull() {
        CommentCreateDto dto = new CommentCreateDto("test-comment-text-1");

        assertNotEquals(null, dto);
    }

    @Test
    void testEqualsWithDifferentClass() {
        CommentCreateDto dto = new CommentCreateDto("test-comment-text-1");

        assertNotEquals("string", dto);
    }
}