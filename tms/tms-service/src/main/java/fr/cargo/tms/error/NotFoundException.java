package fr.cargo.tms.error;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}
