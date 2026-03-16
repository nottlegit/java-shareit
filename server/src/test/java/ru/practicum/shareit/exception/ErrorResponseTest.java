package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void shouldCreateErrorResponseWithError() {
        ErrorResponse response = new ErrorResponse("test-error-message");

        assertNotNull(response);
        assertEquals("test-error-message", response.getError());
    }

    @Test
    void shouldCreateErrorResponseWithNullError() {
        ErrorResponse response = new ErrorResponse(null);

        assertNotNull(response);
        assertNull(response.getError());
    }


    @Test
    void testEqualsWithSameObject() {
        ErrorResponse response = new ErrorResponse("test");
        assertEquals(response, response);
    }

    @Test
    void testEqualsWithNull() {
        ErrorResponse response = new ErrorResponse("test");
        assertNotEquals(null, response);
    }

    @Test
    void testEqualsWithDifferentClass() {
        ErrorResponse response = new ErrorResponse("test");
        assertNotEquals("string", response);
    }
}