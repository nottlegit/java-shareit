package ru.practicum.shareit.request.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestCreateDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void shouldCreateItemRequestCreateDtoUsingConstructor() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("test-request-description-1");

        assertNotNull(dto);
        assertEquals("test-request-description-1", dto.getDescription());
    }

    @Test
    void shouldCreateEmptyItemRequestCreateDtoUsingNoArgsConstructor() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();

        assertNotNull(dto);
        assertNull(dto.getDescription());
    }

    @Test
    void shouldSetAndGetDescription() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("test-request-description-1");

        assertEquals("test-request-description-1", dto.getDescription());
    }

    @Test
    void shouldPassValidationWhenDescriptionIsNotEmpty() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("test-request-description-1");

        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldPassValidationWhenDescriptionHasLeadingAndTrailingSpaces() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("  test-request-description-1  ");

        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testEqualsWithSameObject() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("test-request-description-1");

        assertEquals(dto, dto);
    }

    @Test
    void testEqualsWithNull() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("test-request-description-1");

        assertNotEquals(null, dto);
    }

    @Test
    void testEqualsWithDifferentClass() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("test-request-description-1");

        assertNotEquals("string", dto);
    }
}