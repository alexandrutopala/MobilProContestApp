package ro.infotop.journeytoself.exceptions.auth;

public class EmailInUseException extends Exception {
    public EmailInUseException(String msg) {
        super(msg);
    }
}
