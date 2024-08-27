package org.unibl.etf.ip2024.exceptions;

public class ProgramAlreadyExistsException extends RuntimeException {
    public ProgramAlreadyExistsException(String message) {
        super(message);
    }
}