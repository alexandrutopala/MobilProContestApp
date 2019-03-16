package ro.infotop.journeytoself.listener;

import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

public interface AuthenticationCallback {
    void onAuthenticationError(int errMsgId, CharSequence errString);

    void onAuthenticationHelp(int helpMsgId, CharSequence helpString);

    void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result);

    void onAuthenticationFailed();
}
