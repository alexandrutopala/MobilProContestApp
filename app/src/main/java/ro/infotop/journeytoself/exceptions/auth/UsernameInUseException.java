package ro.infotop.journeytoself.exceptions.auth;

public class UsernameInUseException extends Exception {
    public UsernameInUseException(String msg) {
        super(msg);
    }
}
