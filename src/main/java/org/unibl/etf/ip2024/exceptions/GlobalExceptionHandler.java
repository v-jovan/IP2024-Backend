package org.unibl.etf.ip2024.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.unibl.etf.ip2024.models.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleSecurityException(HttpServletRequest request, Exception exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setPath(request.getRequestURI());

        if (exception instanceof BadCredentialsException) {
            errorResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            errorResponse.setError(HttpStatus.UNAUTHORIZED.getReasonPhrase());
            errorResponse.setMessage("Kredencijali nisu ispravni");
        } else if (exception instanceof AccountStatusException) {
            errorResponse.setStatus(HttpStatus.FORBIDDEN.value());
            errorResponse.setError(HttpStatus.FORBIDDEN.getReasonPhrase());
            errorResponse.setMessage("Nalog je blokiran");
        } else if (exception instanceof AccessDeniedException) {
            errorResponse.setStatus(HttpStatus.FORBIDDEN.value());
            errorResponse.setError(HttpStatus.FORBIDDEN.getReasonPhrase());
            errorResponse.setMessage("Nemate persmisija za pristup ovom resursu");
        } else if (exception instanceof SignatureException || exception instanceof ExpiredJwtException) {
            errorResponse.setStatus(HttpStatus.FORBIDDEN.value());
            errorResponse.setError(HttpStatus.FORBIDDEN.getReasonPhrase());
            errorResponse.setMessage(exception instanceof SignatureException ? "JWT token nije validan" : "JWT token je istekao");
        } else {
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            errorResponse.setMessage("Doslo je do greske prilikom obrade zahtjeva");
        }

        return ResponseEntity
                .status(errorResponse.getStatus())
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(errorResponse);
    }
}

