package ro.infotop.journeytoself;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import ro.infotop.journeytoself.listener.AuthenticationCallback;
import ro.infotop.journeytoself.listener.FragmentCallback;
import ro.infotop.journeytoself.model.userModel.User;
import ro.infotop.journeytoself.service.UserController;
import ro.infotop.journeytoself.utils.FingerprintAuthenticator;
import ro.infotop.journeytoself.utils.SharedPreferencesManager;

public class QuickLoginDialog extends DialogFragment implements AuthenticationCallback {
    public static final String TAG = QuickLoginDialog.class.getSimpleName();

    private EditText passwordEditText;
    private SwitchCompat requestCredentialsSwitch;
    private View fingerprintLayout;
    private TextView loggedUserTextView;
    private TextView errorTextView;

    private User user;
    private FragmentCallback callback;

    private FingerprintAuthenticator authenticator;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_quick_login, container, false);

        try {
            user = SharedPreferencesManager.getLoggedUser(getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (user == null) {
            dismiss();
            return null;
        }

        authenticator = new FingerprintAuthenticator(getContext());

        assignViews(layout);
        setUpGui();

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        startFingerprintAuthentication();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FragmentCallback) {
            callback = (FragmentCallback) context;
        }
    }

    private void assignViews(@NotNull View layout) {
        passwordEditText = layout.findViewById(R.id.password1);
        requestCredentialsSwitch = layout.findViewById(R.id.request_credentials_switch1);
        fingerprintLayout = layout.findViewById(R.id.fingerprint_layout);
        loggedUserTextView = layout.findViewById(R.id.logged_user_textview);
        errorTextView = layout.findViewById(R.id.error_textview);

        Button dismissButton = layout.findViewById(R.id.dismiss_dialog_button);
        Button loginButton = layout.findViewById(R.id.login_button);

        dismissButton.setOnClickListener(this::dismissDialogEvent);
        loginButton.setOnClickListener(this::loginEvent);

        errorTextView.setText("");
    }

    private void setUpGui() {
        loggedUserTextView.setText(user.getUsername());
        initFingerprint();

        boolean requestCredentials = SharedPreferencesManager.isRequestingAuthentication(getContext());
        requestCredentialsSwitch.setChecked(requestCredentials);
    }

    private void initFingerprint() {
        // TODO: check fingerprint authentication
        if (!authenticator.deviceMeetsAllRequirements()) {
            fingerprintLayout.setVisibility(View.GONE);

        } else {
            try {
                authenticator.init();
                fingerprintLayout.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
                fingerprintLayout.setVisibility(View.GONE);
            }
        }
    }

    private void startFingerprintAuthentication() {
        if (authenticator.isSuccessfulInitialized()) {
            authenticator.authenticate(this);
        } else {
            errorTextView.setText(R.string.error_unusasble_fingerprint);
        }
    }

    private void dismissDialogEvent(View view) {
        dismiss();
    }

    private void loginEvent(View view) {
        passwordEditText.setError(null);

        String pass = passwordEditText.getText().toString();
        boolean rez = SharedPreferencesManager.locallyCheckUserCredentials(getContext(), pass);

        if (rez) {
            login();
            dismiss();
        } else {
            passwordEditText.setError(getString(R.string.error_incorrect_password));
            passwordEditText.setText(null);
        }
    }

    private void login() {
        UserController.getInstance().setLoggedUser(user);
        boolean requestCredentials = requestCredentialsSwitch.isChecked();
        SharedPreferencesManager.setRequestAuthentication(getContext(), requestCredentials);
        if (callback != null) {
            callback.call(user, Activity.RESULT_OK);
        }
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        errorTextView.setText(R.string.error_fingerprint_refused);
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        errorTextView.setText(helpString);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
        login();
    }

    @Override
    public void onAuthenticationFailed() {
        errorTextView.setText(R.string.message_try_again);
    }
}
