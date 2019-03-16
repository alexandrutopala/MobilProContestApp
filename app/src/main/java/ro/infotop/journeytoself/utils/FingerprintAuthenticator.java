package ro.infotop.journeytoself.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import ro.infotop.journeytoself.exceptions.InitializationException;
import ro.infotop.journeytoself.exceptions.auth.AuthenticationException;
import ro.infotop.journeytoself.listener.AuthenticationCallback;

public final class FingerprintAuthenticator {

    private final static String KEY_NAME = "jtsKey";
    private KeyStore keyStore;
    private Cipher cipher;
    private Context context;

    private boolean successfulInitialized;

    public FingerprintAuthenticator(Context context) {
        this.context = context;
    }

    public boolean isSdkVersionSupported() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    public boolean isFingerprintEnrolled() {
        return FingerprintManagerCompat.from(context).hasEnrolledFingerprints();
    }

    public boolean isHardwareSupported() {
        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(context);
        return fingerprintManager.isHardwareDetected();
    }

    public boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.checkSelfPermission(Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public boolean deviceMeetsAllRequirements() {
        return isFingerprintEnrolled() &&
               isHardwareSupported() &&
               isSdkVersionSupported() &&
               hasPermission();
    }

    public void init() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            genKey();
            cipherInit();
            successfulInitialized = true;
        }
    }

    public boolean isSuccessfulInitialized() {
        return successfulInitialized;
    }

    public void authenticate(AuthenticationCallback callback) {
        if (!deviceMeetsAllRequirements()) {
            throw new AuthenticationException("Fingerprints not supported");
        }

        if (keyStore == null || cipher == null) {
            throw new InitializationException("Instance not initialized. Try calling init() method");
        }

        FingerprintManagerCompat.CryptoObject cryptoObject = new FingerprintManagerCompat.CryptoObject(cipher);
        FingerprintManagerCompat manager = FingerprintManagerCompat.from(context);
        CancellationSignal cancellationSignal = new CancellationSignal();

        FingerprintManagerCompat.AuthenticationCallback authenticationCallback = new FingerprintManagerCompat.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) {
                super.onAuthenticationError(errMsgId, errString);
                callback.onAuthenticationError(errMsgId, errString);
            }

            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                super.onAuthenticationHelp(helpMsgId, helpString);
                callback.onAuthenticationHelp(helpMsgId, helpString);
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                callback.onAuthenticationSucceeded(result);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                callback.onAuthenticationFailed();
            }
        };

        manager.authenticate(cryptoObject, 0, cancellationSignal, authenticationCallback, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void cipherInit() throws Exception {
        cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES+"/"+KeyProperties.BLOCK_MODE_CBC+"/"+KeyProperties.ENCRYPTION_PADDING_PKCS7);

        keyStore.load(null);
        SecretKey key = (SecretKey)keyStore.getKey(KEY_NAME,null);
        cipher.init(Cipher.ENCRYPT_MODE,key);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void genKey() throws Exception {

        keyStore = KeyStore.getInstance("AndroidKeyStore");

        KeyGenerator keyGenerator = null;

        keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");

        keyStore.load(null);
        keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(true)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7).build()
        );
        keyGenerator.generateKey();
    }

}

