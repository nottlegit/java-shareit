package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IllegalArgumentExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        String message = "test-illegal-argument-message";
        IllegalArgumentException exception = new IllegalArgumentException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldCreateExceptionWithNullMessage() {
        IllegalArgumentException exception = new IllegalArgumentException(null);

        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    @Test
    void shouldBeInstanceOfRuntimeException() {
        IllegalArgumentException exception = new IllegalArgumentException("test");

        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void shouldPreserveStackTrace() {
        IllegalArgumentException exception = new IllegalArgumentException("test");

        assertNotNull(exception.getStackTrace());
    }

    @Test
    void testExceptionThrowing() {
        String message = "test-exception-throw";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            throw new IllegalArgumentException(message);
        });

        assertEquals(message, exception.getMessage());
    }

    @Test
    void testExceptionThrowingWithNullMessage() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            throw new IllegalArgumentException(null);
        });

        assertNull(exception.getMessage());
    }
}