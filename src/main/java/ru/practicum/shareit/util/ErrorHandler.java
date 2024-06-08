package ru.practicum.shareit.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.util.exception.BadRequestException;
import ru.practicum.shareit.util.exception.BookingNotFoundException;
import ru.practicum.shareit.util.exception.BookingOwnerException;
import ru.practicum.shareit.util.exception.ItemNotFoundException;
import ru.practicum.shareit.util.exception.OwnerException;
import ru.practicum.shareit.util.exception.RequestNotFoundException;
import ru.practicum.shareit.util.exception.EmailAlreadyExistException;
import ru.practicum.shareit.util.exception.UserNotFoundException;

import javax.validation.ValidationException;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({UserNotFoundException.class, ItemNotFoundException.class,
            BookingOwnerException.class, BookingNotFoundException.class, RequestNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final BadRequestException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailAlreadyExistException(final EmailAlreadyExistException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleOwnerException(final OwnerException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAnnotationException(final MethodArgumentNotValidException e) {
        return new ErrorResponse(e.getMessage());
    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ErrorResponse handleException(final Throwable e) {
//        return new ErrorResponse("Произошла непредвиденная ошибка");
//    }
}
