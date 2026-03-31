package ru.practicum.exception.handler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.exception.*;

import java.lang.IllegalStateException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAccessDeniedForUserException(final AccessDeniedForUserException e) {
        log.warn("Access denied for user: {}", e.getMessage());
        return new ErrorResponse("BAD_REQUEST");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalStateException(final IllegalStateException e) {
        log.warn("Illegal state: {}", e.getMessage());
        return new ErrorResponse("BAD_REQUEST");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.warn("Resource not found: {}", e.getMessage());
        return new ErrorResponse("NOT_FOUND");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnexpectedNullException(final UnexpectedNullException e) {
        log.warn("Unexpected null value: {}", e.getMessage());
        return new ErrorResponse("BAD_REQUEST");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.warn("Validation failed: {}", e.getMessage());
        return new ErrorResponse("BAD_REQUEST");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataConflictException(final DataConflictException e) {
        log.warn("Data conflict: {}", e.getMessage());
        return new ErrorResponse("CONFLICT");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(final ConflictException e) {
        log.warn("Conflict: {}", e.getMessage());
        return new ErrorResponse("CONFLICT");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        log.warn("Constraint violation: {}", e.getMessage());
        return new ErrorResponse("BAD_REQUEST");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("Method argument not valid: {}", e.getMessage());
        return new ErrorResponse("BAD_REQUEST");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        log.warn("Method argument type mismatch: {}", e.getMessage());
        return new ErrorResponse("BAD_REQUEST");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameterException(
            final MissingServletRequestParameterException e) {
        log.warn("Missing request parameter: {}", e.getMessage());
        return new ErrorResponse("BAD_REQUEST");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUncaught(final Exception e) {
        log.error("Unhandled server error: {}", e.getMessage());
        return new ErrorResponse("INTERNAL_SERVER_ERROR");
    }

}
