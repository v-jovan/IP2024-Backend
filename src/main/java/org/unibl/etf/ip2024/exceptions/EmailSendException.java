package org.unibl.etf.ip2024.exceptions;

public class EmailSendException extends RuntimeException {
    public EmailSendException(String message, Throwable cause) {
        super(message, cause);
    }
}
