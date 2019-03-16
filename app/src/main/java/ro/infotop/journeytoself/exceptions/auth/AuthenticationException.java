package ro.infotop.journeytoself.exceptions.auth;

import ro.infotop.journeytoself.R;
import ro.infotop.journeytoself.service.ResourceController;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException() {
        super(
                ResourceController.getInstance().getResource().getString(R.string.error_unknown_cause)
        );
    }

    public AuthenticationException(String message) {
        super(message);
    }
}
