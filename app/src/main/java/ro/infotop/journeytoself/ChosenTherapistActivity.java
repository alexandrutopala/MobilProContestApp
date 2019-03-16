package ro.infotop.journeytoself;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import ro.infotop.journeytoself.commons.Disease;
import ro.infotop.journeytoself.model.therapyModel.TherapyRequest;
import ro.infotop.journeytoself.model.userModel.Patient;
import ro.infotop.journeytoself.model.userModel.Therapist;
import ro.infotop.journeytoself.model.userModel.User;
import ro.infotop.journeytoself.service.RequestController;
import ro.infotop.journeytoself.service.UserController;
import ro.infotop.journeytoself.utils.Callback;
import ro.infotop.journeytoself.utils.DateUtils;

public class ChosenTherapistActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<TherapyRequest> {

    private static final int REQUEST_LOADER_ID = 1;
    private boolean FIRST_LOAD_FLAG = true;

    private Patient user;
    private TherapyRequest request;
    private ProgressBar progressBar;
    private TextView dateTextView;
    private TextView statusTextView;

    private RequestCancellerTask requestCancellerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosen_therapist);

        User user = UserController.getInstance().getLoggedUser();
        if (!(user instanceof Patient)) {
            finish();
            return;
        }
        this.user = (Patient) user;

        if (this.user.getTherapist() == null) {
            finish();
            return;
        }

        assignViewAndPopulateGui(((Patient) user).getTherapist());
        startLoading();
    }

    private void assignViewAndPopulateGui(Therapist t) {
        View itemView = findViewById(R.id.therapist_profile_layout);
        TextView nameTextView = itemView.findViewById(R.id.name_textview1);
        TextView locationTextView = itemView.findViewById(R.id.location_textview);
        TextView enumLabelTextView = itemView.findViewById(R.id.enumeration_label_textview1);
        TextView enumTextView = itemView.findViewById(R.id.enumeration_textview1);
        TextView descriptionTextView = itemView.findViewById(R.id.description_textview1);
        enumLabelTextView.setText(R.string.label_specializations);
        ImageView profileImage = itemView.findViewById(R.id.profile_image);

        dateTextView = findViewById(R.id.emitted_date_textview1);
        statusTextView = findViewById(R.id.request_status_textview);
        progressBar = findViewById(R.id.progress_load_request);

        nameTextView.setText(t.getName());
        locationTextView.setText(String.format("%s, %s", t.getCity(), t.getCountry()));

        StringBuilder sb = new StringBuilder();
        for (Disease illness : t.getSpecializations()) {
            sb.append(illness);
            sb.append(", ");
        }

        if (sb.length() > 2) sb.delete(sb.length() - 2, sb.length());
        enumTextView.setText(sb.toString());

        descriptionTextView.setText(t.getDescription());
    }

    public void cancelButtonEvent(View view) {
        if (requestCancellerTask == null) {
            requestCancellerTask = new RequestCancellerTask(
                    this,
                    () -> {
                        requestCancellerTask = null;
                        finish();
                    }
            );

            requestCancellerTask.execute(user, user.getTherapist());
        }
    }

    public void therapistProfileEvent(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.ACTION_VIEW_THERAPIST_PROFILE, true);
        intent.putExtra(ProfileActivity.FLAG_HIDE_CHOOSE_BUTTON_KEY, true);
        startActivity(intent);
    }

    private void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
        if (FIRST_LOAD_FLAG) {
            FIRST_LOAD_FLAG = false;
            getSupportLoaderManager().initLoader(REQUEST_LOADER_ID, null, this).forceLoad();
        } else {
            getSupportLoaderManager().restartLoader(REQUEST_LOADER_ID, null, this).forceLoad();
        }
    }

    @NonNull
    @Override
    public Loader<TherapyRequest> onCreateLoader(int i, @Nullable Bundle bundle) {
        if (i == REQUEST_LOADER_ID) {
            return new LoadRequestTask(this, user, user.getTherapist());
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<TherapyRequest> loader, TherapyRequest therapyRequest) {
        progressBar.setVisibility(View.GONE);
        this.request = therapyRequest;
        dateTextView.setText(String.format("%s %s", getString(R.string.label_emitted_date), DateUtils.parseDateAndTime(request.getTimeStamp())));
        statusTextView.setText(String.format("%s %s", getString(R.string.status_prefix), request.getStatus()));
    }

    @Override
    public void onLoaderReset(@NonNull Loader<TherapyRequest> loader) {

    }

    private static class LoadRequestTask extends AsyncTaskLoader<TherapyRequest> {

        private Patient patient;
        private Therapist patientsTherapist;

        public LoadRequestTask(@NonNull Context context, Patient patient, Therapist patientsTherapist) {
            super(context);
            this.patient = patient;
            this.patientsTherapist = patientsTherapist;
        }

        @Nullable
        @Override
        public TherapyRequest loadInBackground() {
            return RequestController.getInstance(getContext()).findSingleRequest(patient, patientsTherapist);
        }
    }

    private static class RequestCancellerTask extends AsyncTask<User, Void, Void> {
        @SuppressLint("StaticFieldLeak")
        private Context context;
        private ProgressDialog progressDialog;
        private Callback onFinish;

        private RequestCancellerTask(Context context, Callback onFinish) {
            this.context = context;
            this.onFinish = onFinish;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(
                    context,
                    context.getString(R.string.title_cancelling_request),
                    "",
                    true,
                    false
            );
        }

        @Override
        protected Void doInBackground(User... users) {
            Patient patient = (Patient) users[0];
            Therapist therapist = (Therapist) users[1];

            RequestController.getInstance(context).cancelRequest(patient, therapist, true);
            UserController.getInstance().updatePatientsTherapist(patient, null);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            onFinish.call();
        }
    }
}
