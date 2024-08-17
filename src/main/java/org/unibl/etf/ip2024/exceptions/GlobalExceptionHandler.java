package org.unibl.etf.ip2024.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.unibl.etf.ip2024.models.dto.response.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(HttpServletRequest request, Exception exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setPath(request.getRequestURI());

        if (exception instanceof BadCredentialsException) {
            errorResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            errorResponse.setError(HttpStatus.UNAUTHORIZED.getReasonPhrase());
            errorResponse.setMessage("Kredencijali nisu ispravni");
            logger.warn("Bad credentials: {}", exception.getMessage());
        } else if (exception instanceof AccountStatusException) {
            errorResponse.setStatus(HttpStatus.FORBIDDEN.value());
            errorResponse.setError(HttpStatus.FORBIDDEN.getReasonPhrase());
            errorResponse.setMessage("Nalog je blokiran");
            logger.warn("Account status exception: {}", exception.getMessage());
        } else if (exception instanceof AccessDeniedException) {
            errorResponse.setStatus(HttpStatus.FORBIDDEN.value());
            errorResponse.setError(HttpStatus.FORBIDDEN.getReasonPhrase());
            errorResponse.setMessage("Nemate persmisija za pristup ovom resursu");
            logger.warn("Access denied: {}", exception.getMessage());
        } else if (exception instanceof SignatureException || exception instanceof ExpiredJwtException) {
            errorResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            errorResponse.setError(HttpStatus.UNAUTHORIZED.getReasonPhrase());
            errorResponse.setMessage(exception instanceof SignatureException ? "JWT token nije validan" : "JWT token je istekao");
            logger.warn("JWT exception: {}", exception.getMessage());
        } else if (exception instanceof IllegalArgumentException) {
            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            errorResponse.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
            errorResponse.setMessage(exception.getMessage());
            logger.warn("Illegal argument: {}", exception.getMessage());
        } else if (exception instanceof UserAlreadyExistsException) {
            errorResponse.setStatus(HttpStatus.CONFLICT.value());
            errorResponse.setError(HttpStatus.CONFLICT.getReasonPhrase());
            errorResponse.setMessage(exception.getMessage());
            logger.warn("User already exists: {}", exception.getMessage());
        } else if (exception instanceof UserNotFoundException) {
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
            errorResponse.setMessage(exception.getMessage());
            logger.warn("User not found: {}", exception.getMessage());
        } else if (exception instanceof InvalidTokenException) {
            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            errorResponse.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
            errorResponse.setMessage(exception.getMessage());
            logger.warn("Invalid token: {}", exception.getMessage());
        } else if (exception instanceof EmailSendException) {
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            errorResponse.setMessage("Greska prilikom slanja emaila: " + exception.getMessage());
            logger.error("Email send exception: {}", exception.getMessage());
        } else if (exception instanceof AccountActivationException) {
            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            errorResponse.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
            errorResponse.setMessage(exception.getMessage());
            logger.warn("Account activation error: {}", exception.getMessage());
        } else if (exception instanceof InvalidOldPasswordException) {
            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            errorResponse.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
            errorResponse.setMessage(exception.getMessage());
            logger.warn("Invalid old password: {}", exception.getMessage());
        } else if (exception instanceof RssFeedException) {
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            errorResponse.setMessage(exception.getMessage());
            logger.error("RSS feed error: {}", exception.getMessage());
        } else if (exception instanceof ExerciseFetchException) {
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            errorResponse.setMessage(exception.getMessage());
            logger.error("Exercise fetch error: {}", exception.getMessage());
        } else {
            errorResponse.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            errorResponse.setError(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase());
            errorResponse.setMessage("Došlo je do greške prilikom obrade zahtjeva");
            logger.error("Internal server error: {}", exception.getMessage());
        }

        return ResponseEntity
                .status(errorResponse.getStatus())
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(errorResponse);
    }
}
