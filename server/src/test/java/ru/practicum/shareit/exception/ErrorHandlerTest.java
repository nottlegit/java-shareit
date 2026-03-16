package ru.practicum.shareit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {

    @InjectMocks
    private ErrorHandler errorHandler;
    private DuplicateEmailException duplicateEmailException;
    private NotFoundException notFoundException;
    private AccessDeniedException accessDeniedException;
    private Throwable throwable;

    @BeforeEach
    void setUp() {
        duplicateEmailException = new DuplicateEmailException("test-duplicate-email-error");
        notFoundException = new NotFoundException("test-not-found-error");
        accessDeniedException = new AccessDeniedException("test-access-denied-error");
        throwable = new RuntimeException("test-internal-error");
    }

    @Test
    void handleDuplicateEmail_ShouldReturnConflict() {
        ErrorResponse response = errorHandler.handleDuplicateEmail(duplicateEmailException);

        assertNotNull(response);
        assertEquals("test-duplicate-email-error", response.getError());
    }

    @Test
    void handleNotFound_ShouldReturnNotFound() {
        ErrorResponse response = errorHandler.handleNotFound(notFoundException);

        assertNotNull(response);
        assertEquals("test-not-found-error", response.getError());
    }

    @Test
    void handleThrowable_ShouldReturnInternalServerError() {
        ErrorResponse response = errorHandler.handleThrowable(throwable);

        assertNotNull(response);
        assertEquals("Произошла непредвиденная ошибка", response.getError());
    }

    @Test
    void handleAccessDeniedException_ShouldReturnForbidden() {
        ErrorResponse response = errorHandler.handleAccessDeniedException(accessDeniedException);

        assertNotNull(response);
        assertEquals("test-access-denied-error", response.getError());
    }

    @Test
    void handleDuplicateEmail_ShouldReturnConflictWithNullMessage() {
        DuplicateEmailException ex = new DuplicateEmailException(null);
        ErrorResponse response = errorHandler.handleDuplicateEmail(ex);

        assertNotNull(response);
        assertNull(response.getError());
    }

    @Test
    void handleNotFound_ShouldReturnNotFoundWithNullMessage() {
        NotFoundException ex = new NotFoundException(null);
        ErrorResponse response = errorHandler.handleNotFound(ex);

        assertNotNull(response);
        assertNull(response.getError());
    }

    @Test
    void handleAccessDeniedException_ShouldReturnForbiddenWithNullMessage() {
        AccessDeniedException ex = new AccessDeniedException(null);
        ErrorResponse response = errorHandler.handleAccessDeniedException(ex);

        assertNotNull(response);
        assertNull(response.getError());
    }

    @Test
    void handleThrowable_ShouldReturnInternalServerErrorWithNullThrowable() {
        ErrorResponse response = errorHandler.handleThrowable(null);

        assertNotNull(response);
        assertEquals("Произошла непредвиденная ошибка", response.getError());
    }

    @Test
    void handleThrowable_ShouldReturnInternalServerErrorWithCustomMessage() {
        Throwable customThrowable = new RuntimeException("custom error");
        ErrorResponse response = errorHandler.handleThrowable(customThrowable);

        assertNotNull(response);
        assertEquals("Произошла непредвиденная ошибка", response.getError());
    }
}