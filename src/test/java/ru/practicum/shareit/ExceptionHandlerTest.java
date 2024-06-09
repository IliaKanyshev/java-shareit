package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.util.ErrorHandler;
import ru.practicum.shareit.util.ErrorResponse;
import ru.practicum.shareit.util.exception.EmailAlreadyExistException;
import ru.practicum.shareit.util.exception.UserNotFoundException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class ExceptionHandlerTest {
    private ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        errorHandler = new ErrorHandler();
    }

    @Test
    void handleNotFoundException() {
        UserNotFoundException e = new UserNotFoundException("Какая-то ошибка");
        ErrorResponse result = errorHandler.handleNotFoundException(e);
        assertNotNull(result);
        assertThat("Какая-то ошибка", equalTo(e.getMessage()));
    }

    @Test
    void handleConflict() {
        EmailAlreadyExistException e = new EmailAlreadyExistException("Какая-то ошибка");
        ErrorResponse result = errorHandler.handleEmailAlreadyExistException(e);
        assertNotNull(result);
        assertThat("Какая-то ошибка", equalTo(e.getMessage()));
    }
}
