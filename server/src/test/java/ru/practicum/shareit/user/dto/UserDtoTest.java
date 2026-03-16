package ru.practicum.shareit.user.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void shouldCreateUserDtoUsingBuilder() {
        UserDto dto = UserDto.builder()
                .id(1L)
                .name("test-user-name-1")
                .email("test-user-1@test.com")
                .build();

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("test-user-name-1", dto.getName());
        assertEquals("test-user-1@test.com", dto.getEmail());
    }

    @Test
    void shouldCreateUserDtoUsingToBuilder() {
        UserDto original = UserDto.builder()
                .id(1L)
                .name("test-user-name-1")
                .email("test-user-1@test.com")
                .build();

        UserDto modified = original.toBuilder()
                .name("test-user-name-updated")
                .build();

        assertEquals(1L, modified.getId());
        assertEquals("test-user-name-updated", modified.getName());
        assertEquals("test-user-1@test.com", modified.getEmail());
    }

    @Test
    void shouldCreateEmptyUserDto() {
        UserDto dto = UserDto.builder().build();

        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getEmail());
    }

    @Test
    void shouldSetAndGetFields() {
        UserDto dto = UserDto.builder().build();

        dto.setId(1L);
        dto.setName("test-user-name-1");
        dto.setEmail("test-user-1@test.com");

        assertEquals(1L, dto.getId());
        assertEquals("test-user-name-1", dto.getName());
        assertEquals("test-user-1@test.com", dto.getEmail());
    }

    @Test
    void shouldFailValidationWhenNameIsNull() {
        UserDto dto = UserDto.builder()
                .name(null)
                .email("test-user-1@test.com")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Имя не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenNameIsBlank() {
        UserDto dto = UserDto.builder()
                .name("   ")
                .email("test-user-1@test.com")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Имя не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenNameIsEmpty() {
        UserDto dto = UserDto.builder()
                .name("")
                .email("test-user-1@test.com")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Имя не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenEmailIsNull() {
        UserDto dto = UserDto.builder()
                .name("test-user-name-1")
                .email(null)
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Электронная почта не может быть пустой", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenEmailIsEmpty() {
        UserDto dto = UserDto.builder()
                .name("test-user-name-1")
                .email("")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Электронная почта не может быть пустой", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenEmailIsInvalid() {
        UserDto dto = UserDto.builder()
                .name("test-user-name-1")
                .email("invalid-email")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Электронная почта должна содержать символ @ и быть валидной", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenEmailIsMissingAtSymbol() {
        UserDto dto = UserDto.builder()
                .name("test-user-name-1")
                .email("test-user-1.test.com")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Электронная почта должна содержать символ @ и быть валидной", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenEmailIsMissingDomain() {
        UserDto dto = UserDto.builder()
                .name("test-user-name-1")
                .email("test-user-1@")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Электронная почта должна содержать символ @ и быть валидной", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWhenEmailIsMissingLocalPart() {
        UserDto dto = UserDto.builder()
                .name("test-user-name-1")
                .email("@test.com")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Электронная почта должна содержать символ @ и быть валидной", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailValidationWithMultipleViolations() {
        UserDto dto = UserDto.builder()
                .name(null)
                .email(null)
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());

        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Имя не может быть пустым")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Электронная почта не может быть пустой")));
    }

    @Test
    void shouldPassValidationWhenAllFieldsAreValid() {
        UserDto dto = UserDto.builder()
                .name("test-user-name-1")
                .email("test-user-1@test.com")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldPassValidationWithComplexEmail() {
        UserDto dto = UserDto.builder()
                .name("test-user-name-1")
                .email("test-user-1+test@subdomain.test.co.uk")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldAllowIdToBeNull() {
        UserDto dto = UserDto.builder()
                .id(null)
                .name("test-user-name-1")
                .email("test-user-1@test.com")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testEqualsAndHashCode() {
        UserDto dto1 = UserDto.builder()
                .id(1L)
                .name("test-user-name-1")
                .email("test-email@test.com")
                .build();

        UserDto dto2 = UserDto.builder()
                .id(2L)
                .name("test-user-name-2")
                .email("test-email@test.com")
                .build();

        UserDto dto3 = UserDto.builder()
                .id(3L)
                .name("test-user-name-3")
                .email("test-email-3@test.com")
                .build();

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testEqualsWithSameObject() {
        UserDto dto = UserDto.builder()
                .id(1L)
                .name("test-user-name-1")
                .email("test-email@test.com")
                .build();

        assertEquals(dto, dto);
    }

    @Test
    void testEqualsWithNull() {
        UserDto dto = UserDto.builder()
                .id(1L)
                .name("test-user-name-1")
                .email("test-email@test.com")
                .build();

        assertNotEquals(null, dto);
    }

    @Test
    void testEqualsWithDifferentClass() {
        UserDto dto = UserDto.builder()
                .id(1L)
                .name("test-user-name-1")
                .email("test-email@test.com")
                .build();

        assertNotEquals("string", dto);
    }

    @Test
    void testToString() {
        UserDto dto = UserDto.builder()
                .id(1L)
                .name("test-user-name-1")
                .email("test-email@test.com")
                .build();

        String toString = dto.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("test-user-name-1"));
        assertTrue(toString.contains("test-email@test.com"));
    }
}