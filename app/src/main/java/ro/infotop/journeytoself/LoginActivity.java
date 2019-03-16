package ro.infotop.journeytoself;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import ro.infotop.journeytoself.exceptions.auth.AuthenticationException;
import ro.infotop.journeytoself.listener.FragmentCallback;
import ro.infotop.journeytoself.model.userModel.User;
import ro.infotop.journeytoself.service.PostsController;
import ro.infotop.journeytoself.service.UserController;
import ro.infotop.journeytoself.utils.SharedPreferencesManager;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements FragmentCallback {
    public static final int REGISTRATION_STATUS_REQUEST_CODE = 0;
    public static final int REGISTRATION_SUCCESSFUL_CODE = 1;
    public static final int REGISTRATION_FAILED_CODE = 2;


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private SwitchCompat mRememberSwitch;
    private SwitchCompat mRequestSwitch;

    private QuickLoginDialog loginDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(view -> attemptLogin());


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mRememberSwitch = findViewById(R.id.remember_switch);
        mRequestSwitch = findViewById(R.id.request_credentials_switch);

        mRememberSwitch.setOnClickListener(v ->  {
            mRequestSwitch.setVisibility(
                mRememberSwitch.isChecked() ? View.VISIBLE : View.GONE
            );

            if (!mRememberSwitch.isChecked()) {
                mRequestSwitch.setChecked(false);
            }
        });

        overridePendingTransition(R.transition.activity_slide_in, R.transition.activity_slide_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (loginDialog == null) {
            setUpCredentials();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REGISTRATION_STATUS_REQUEST_CODE) {
            if (resultCode == REGISTRATION_SUCCESSFUL_CODE) {
                User user = (User) data.getSerializableExtra(RegisterActivity.USER_KEY);
                mUsernameView.setText(user.getUsername());
                Toast.makeText(this, R.string.message_registration_complete, Toast.LENGTH_SHORT).show();
            } else if (resultCode == REGISTRATION_FAILED_CODE) {
                Toast.makeText(this, R.string.message_registration_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void call(User user, int resultCode) {
        if (resultCode == RESULT_OK) {
            persistCredentials(user, null);
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            finish();

        }
        loginDialog = null;
    }

    private void setUpCredentials() {
        boolean rememberCredentials = SharedPreferencesManager.isRememberingCredentials(this);
        mRememberSwitch.setChecked(rememberCredentials);

        if (rememberCredentials) {
            mRequestSwitch.setVisibility(View.VISIBLE);
            boolean requestPassOrFingerprint = SharedPreferencesManager.isRequestingAuthentication(this);

            User user;

            try {
                user = SharedPreferencesManager.getLoggedUser(this);
            } catch (IOException e) {
                e.printStackTrace();
                mRequestSwitch.setVisibility(View.GONE);
                mRememberSwitch.setChecked(false);
                Toast.makeText(this, R.string.error_credentials_unsaved, Toast.LENGTH_SHORT).show();
                return;
            }

            if (requestPassOrFingerprint) {
                mUsernameView.setText(user.getUsername());
                mRequestSwitch.setChecked(true);
                loginDialog = new QuickLoginDialog();
                loginDialog.setCancelable(false);
                loginDialog.show(getSupportFragmentManager(), QuickLoginDialog.TAG);

            } else {
                UserController.getInstance().setLoggedUser(user);
                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
                return;
            }

        } else {
            mRequestSwitch.setVisibility(View.GONE);
        }
    }

    private void persistCredentials(User user, @Nullable String pass) {
        // TODO: save preferences and credentials
        boolean rememberCredentials = mRememberSwitch.isChecked();
        boolean requestCredentials = mRequestSwitch.isChecked();

        SharedPreferencesManager.setRememberCredentials(this, rememberCredentials);
        SharedPreferencesManager.setRequestAuthentication(this, requestCredentials);

        if (rememberCredentials && pass != null) {
            try {
                SharedPreferencesManager.setLoggedUser(this, user, pass);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (!rememberCredentials) {
            SharedPreferencesManager.removeAllUserPreferences(this);
        }
    }

    public void attemptRegister(View view) {
        // TODO: do register activity
        String mUsername = mUsernameView.getText().toString();
        String mPassword = mPasswordView.getText().toString();

        Intent intent = new Intent(this, ChooseKindActivity.class);
        startActivityForResult(intent, REGISTRATION_STATUS_REQUEST_CODE);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isUsernameValid(String username) {
        //TODO: Replace this with your own logic
        return !username.isEmpty();
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 6;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }



    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, User> {

        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected User doInBackground(Void... params) {
            try {
                User user =  UserController.getInstance().loginUser(mUsername, mPassword);

                return user;
            } catch (AuthenticationException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final User user) {
            mAuthTask = null;
            showProgress(false);

            if (user != null) {
                persistCredentials(user, mPassword);
                UserController.getInstance().setLoggedUser(user);
                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_credentials));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

