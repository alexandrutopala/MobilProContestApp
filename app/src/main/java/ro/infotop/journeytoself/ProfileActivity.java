package ro.infotop.journeytoself;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ro.infotop.journeytoself.commons.Disease;
import ro.infotop.journeytoself.model.userModel.Patient;
import ro.infotop.journeytoself.model.userModel.Therapist;
import ro.infotop.journeytoself.model.userModel.User;
import ro.infotop.journeytoself.service.RequestController;
import ro.infotop.journeytoself.service.UserController;
import ro.infotop.journeytoself.utils.ParametricCallback;
import ro.infotop.journeytoself.utils.SharedPreferencesManager;

public class ProfileActivity extends AppCompatActivity {
    public static final String KEY_USER = "user";
    public static final String ACTION_VIEW_THERAPIST_PROFILE = "viewTherapeutProfile";
    public static final String FLAG_HIDE_CHOOSE_BUTTON_KEY = "ascundelllbaaa";

    private boolean FLAG_CHOOSING_THERAPIST = false;
    private boolean FLAG_HIDE_CHOOSE_BUTTON;

    private User user;

    private ImageView profilePicture;
    private TextView nameTextView;
    private TextView localTextView;
    private TextView overallScoreTextView;
    private TextView todayProgresstextView;
    private TextView emailTextView;
    private TextView usernameTextView;
    private TextView statutTextView;
    private TextView enumerationLabelTextView;
    private TextView enumerationTextView;
    private LinearLayout addressLayout;
    private TextView addressLabelTextView;
    private TextView addressTextView;
    private LinearLayout descriptionLayout;
    private TextView descriptionLabelTextView;
    private TextView descriptionTextView;

    private SendRequestTask sendRequestTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = UserController.getInstance().getLoggedUser();
        if (user == null) {
            finish();
            return;
        }

        if (user instanceof Patient) {
            boolean viewTherapistProfile = getIntent().getBooleanExtra(ACTION_VIEW_THERAPIST_PROFILE, false);
            if (viewTherapistProfile) {
                FLAG_HIDE_CHOOSE_BUTTON = getIntent().getBooleanExtra(FLAG_HIDE_CHOOSE_BUTTON_KEY, false);
                startTherapistModeFromPatientPerspective();
                user = ((Patient) user).getTherapist();
            } else {
                startPatientMode();
            }
        } else if (user instanceof Therapist) {
            Therapist t = (Therapist) user;
            if (t.getKeyPatient() == null) {
                startTherapistMode();
            } else {
                user = t.getKeyPatient();
                startPatientModeFromTherapistPerspective();
            }
        }

        assignViews();
        fillGui(user);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (UserController.getInstance().getLoggedUser() instanceof Patient) {
            NewsFeedActivity.requestRefreshArticles();
        }
    }

    @Override
    public void onBackPressed() {
        if (FLAG_CHOOSING_THERAPIST) {
            setResult(RESULT_CANCELED);
        }
        super.onBackPressed();
    }

    private void fillGui(User user) {
        nameTextView.setText(user.getName());
        localTextView.setText(user.getCity() + ", " + user.getCountry());
        emailTextView.setText(user.getEmail());
        usernameTextView.setText(user.getUsername());
        statutTextView.setText(
               user instanceof Therapist ? R.string.therapist : R.string.patient
        );

        enumerationLabelTextView.setText(
                user instanceof Therapist ? R.string.label_specializations : R.string.label_illnesses
        );

        StringBuilder sb = new StringBuilder();
        for (Disease illness : user instanceof Therapist ? ((Therapist) user).getSpecializations() : ((Patient) user).getDiseases()) {
            sb.append(illness);
            sb.append(", ");
        }

        if (sb.length() > 2) sb.delete(sb.length() - 2, sb.length());
        enumerationTextView.setText(sb.toString());

        if (user instanceof Therapist) {
            descriptionLayout.setVisibility(View.VISIBLE);
            addressLayout.setVisibility(View.VISIBLE);
            addressTextView.setText(((Therapist) user).getCabinetAddress());
            descriptionTextView.setText(((Therapist) user).getDescription());
        } else if (user instanceof Patient) {
            descriptionLayout.setVisibility(View.GONE);
            addressLayout.setVisibility(View.GONE);
        }
    }

    private void assignViews() {
        profilePicture = findViewById(R.id.profile_image);
        nameTextView = findViewById(R.id.name_textview);
        localTextView = findViewById(R.id.tv_address);
        overallScoreTextView = findViewById(R.id.overall_score_textview);
        todayProgresstextView = findViewById(R.id.today_progress_textview);
        emailTextView = findViewById(R.id.email_textview);
        usernameTextView = findViewById(R.id.username_textview);
        statutTextView = findViewById(R.id.statut_textview);
        enumerationLabelTextView = findViewById(R.id.enumeration_label_textview);
        enumerationTextView = findViewById(R.id.enumeration_textview);
        addressLayout = findViewById(R.id.address_layout);
        addressLabelTextView = findViewById(R.id.address_label_textview);
        addressTextView = findViewById(R.id.address_textview);
        descriptionLayout = findViewById(R.id.description_layout);
        descriptionLabelTextView = findViewById(R.id.description_label_textview);
        descriptionTextView = findViewById(R.id.description_textview);
    }

    public void backToNewsFeed(View view) {
        setResult(RESULT_OK);
        finish();
    }

    public void goToProgress(View view) {
        // TODO: go to progress activity
        Intent intent = new Intent(this, ProgressActivity.class);
        startActivity(intent);
    }

    public void goToTherapists(View view) {
        // TODO: go to therapists activity
        String buttonText = ((Button) view).getText().toString();

        if (buttonText.equals(getString(R.string.text_to_therapists))) {
            // view therapists as a patient
            if (((Patient) user).getTherapist() == null) {
                Intent intent = new Intent(this, TherapistsViewActivity.class);
                startActivity(intent);
            } else {
                // view chosen therapist
                Intent intent = new Intent(this, ChosenTherapistActivity.class);
                startActivity(intent);
            }
        } else if (buttonText.equals(getString(R.string.text_to_patients))) {
            // view patients as a therapist
            Intent intent = new Intent(this, PatientsViewActivity.class);
            startActivity(intent);
        }
    }

    public void logout(View view) {
        SharedPreferencesManager.removeAllUserPreferences(this);
        UserController.getInstance().setLoggedUser(null);
        finish();
    }

    /////////////////// Activity mode (therapist or patient)

    private void startTherapistMode() {
        Button news = findViewById(R.id.newsfeed_button);
        Button progress = findViewById(R.id.progress_button);
        Button entities = findViewById(R.id.patients_or_therapists_button);


        TextView scoreOrRating = findViewById(R.id.overall_score_textview);
        TextView scoreOrRatingLabel = findViewById(R.id.score_label_textview);

        TextView progressOrPatientsCount = findViewById(R.id.today_progress_textview);
        TextView progressOrPatientsCountLabel = findViewById(R.id.today_progress_label_textview);


        news.setText(R.string.text_to_newsfeed);
        progress.setVisibility(View.GONE);
        entities.setText(R.string.text_to_patients);

        scoreOrRatingLabel.setText(R.string.text_rating);
        progressOrPatientsCountLabel.setText(R.string.text_current_patients);


        // TODO: add text for score and progress
    }

    private void startPatientMode() {
        Button news = findViewById(R.id.newsfeed_button);
        Button progress = findViewById(R.id.progress_button);
        Button entities = findViewById(R.id.patients_or_therapists_button);
        ImageButton logout = findViewById(R.id.logout_button);

        TextView scoreOrRating = findViewById(R.id.overall_score_textview);
        TextView scoreOrRatingLabel = findViewById(R.id.score_label_textview);

        TextView progressOrPatientsCount = findViewById(R.id.today_progress_textview);
        TextView progressOrPatientsCountLabel = findViewById(R.id.today_progress_label_textview);

        news.setText(R.string.text_to_newsfeed);
        progress.setText(R.string.text_progress);
        entities.setText(R.string.text_to_therapists);

        scoreOrRatingLabel.setText(R.string.text_overall_score);
        progressOrPatientsCountLabel.setText(R.string.text_today_progress);

        // TODO: add text for score and progress
    }

    private void startPatientModeFromTherapistPerspective() {
        Button news = findViewById(R.id.newsfeed_button);
        Button progress = findViewById(R.id.progress_button);
        Button entities = findViewById(R.id.patients_or_therapists_button);
        ImageButton logout = findViewById(R.id.logout_button);
        View notificationFragment = findViewById(R.id.notification_fragment);
        View bottomSpacer = findViewById(R.id.bottom_spacer);

        TextView scoreOrRating = findViewById(R.id.overall_score_textview);
        TextView scoreOrRatingLabel = findViewById(R.id.score_label_textview);

        TextView progressOrPatientsCount = findViewById(R.id.today_progress_textview);
        TextView progressOrPatientsCountLabel = findViewById(R.id.today_progress_label_textview);

        bottomSpacer.setVisibility(View.GONE);
        notificationFragment.setVisibility(View.GONE);
        news.setVisibility(View.GONE);
        progress.setText(R.string.text_progress);
        entities.setVisibility(View.GONE);
        logout.setVisibility(View.INVISIBLE);

        scoreOrRatingLabel.setText(R.string.text_overall_score);
        progressOrPatientsCountLabel.setText(R.string.text_today_progress);

        // TODO: add text for score and progress
    }

    private void startTherapistModeFromPatientPerspective() {

        FLAG_CHOOSING_THERAPIST = true;

        Button news = findViewById(R.id.newsfeed_button);
        Button progress = findViewById(R.id.progress_button);
        Button entities = findViewById(R.id.patients_or_therapists_button);
        ImageButton logout = findViewById(R.id.logout_button);
        Button choose = findViewById(R.id.choose_button);
        View notificationFragment = findViewById(R.id.notification_fragment);
        View bottomSpacer = findViewById(R.id.bottom_spacer);

        TextView scoreOrRating = findViewById(R.id.overall_score_textview);
        TextView scoreOrRatingLabel = findViewById(R.id.score_label_textview);

        TextView progressOrPatientsCount = findViewById(R.id.today_progress_textview);
        TextView progressOrPatientsCountLabel = findViewById(R.id.today_progress_label_textview);

        bottomSpacer.setVisibility(View.GONE);
        notificationFragment.setVisibility(View.GONE);
        news.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        entities.setVisibility(View.GONE);
        logout.setVisibility(View.INVISIBLE);
        if (!FLAG_HIDE_CHOOSE_BUTTON) {
            choose.setVisibility(View.VISIBLE);
        }

        scoreOrRatingLabel.setText(R.string.text_rating);
        progressOrPatientsCountLabel.setText(R.string.text_current_patients);


        // TODO: add text for score and progress
    }

    public void therapistChosen(View view) {
        if (sendRequestTask == null) {
            sendRequestTask = new SendRequestTask(this, (b) -> {
                if (b) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(ProfileActivity.this, R.string.error_connot_send_request, Toast.LENGTH_LONG).show();
                }
                sendRequestTask = null;
            });
            sendRequestTask.execute(
                    UserController.getInstance().getLoggedUser(),
                    user
            );
        }
    }

    private static class SendRequestTask extends AsyncTask<User, Void, Boolean> {
        @SuppressLint("StaticFieldLeak")
        private Context context;
        private ProgressDialog progressDialog;
        private ParametricCallback<Boolean> onFinish;

        private SendRequestTask(Context context, ParametricCallback<Boolean> onFinish) {
            this.context = context;
            this.onFinish = onFinish;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(
                    context,
                    context.getString(R.string.message_sending_request),
                    "",
                    true,
                    false
            );
        }

        @Override
        protected Boolean doInBackground(User... longs) {
            Patient patient = (Patient) longs[0];
            Therapist therapist = (Therapist) longs[1];
            try {
                RequestController.getInstance(context).sendRequest(patient, therapist);
                UserController.getInstance().updatePatientsTherapist(patient, therapist);
            } catch (RuntimeException re) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressDialog.dismiss();
            onFinish.call(aBoolean);
        }
    }
}
