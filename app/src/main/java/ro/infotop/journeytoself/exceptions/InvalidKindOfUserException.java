package ro.infotop.journeytoself.exceptions;

public class InvalidKindOfUserException extends RuntimeException {
    public InvalidKindOfUserException() {
        super("The user is neither a patient or a therapist");
    }
}
