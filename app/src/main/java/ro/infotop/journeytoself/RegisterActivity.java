package ro.infotop.journeytoself;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ro.infotop.journeytoself.adapter.DiseaseAdapter;
import ro.infotop.journeytoself.commons.Disease;
import ro.infotop.journeytoself.exceptions.auth.EmailInUseException;
import ro.infotop.journeytoself.exceptions.auth.UsernameInUseException;
import ro.infotop.journeytoself.model.userModel.Patient;
import ro.infotop.journeytoself.model.userModel.Therapist;
import ro.infotop.journeytoself.model.userModel.User;
import ro.infotop.journeytoself.service.IllnessesController;
import ro.infotop.journeytoself.service.ResourceController;
import ro.infotop.journeytoself.service.UserController;

public class RegisterActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<User> {
    private static final int DISEASE_LIST_REQUEST_CODE = 3;
    private static final int REGISTER_LOADER_ID = 1;
    private static final int ILLNESSES_LOADER_ID = 2;
    public static final String USER_KEY = "registeredUser";

    private RecyclerView recyclerView;
    private DiseaseAdapter adapter;
    private ProgressBar registrationProgress;
    private ProgressBar illnessesProgress;
    private int kindCode;
    private ErrorPublisherHandler publishHandler = new ErrorPublisherHandler();

    private EditText usernameView;
    private EditText emailView;
    private EditText nameView;
    private EditText passView;
    private EditText confirmPassView;
    private EditText descriptionView;
    private EditText addressView;
    private Spinner countrySpinner;
    private Spinner citySpinner;

    private boolean FIRST_TIME_LOADED = true;
    private boolean FIRST_TIME_RETRIEVE_ILLNESSES = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        kindCode = getIntent().getIntExtra(ChooseKindActivity.KIND_KEY, ChooseKindActivity.PATIENT_CODE);
        checkKindOrFinish(kindCode);

        assignViews();
        setUpRecycler();
        setUpSpinners();

        setUpGUI(kindCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == DISEASE_LIST_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                List<Disease> diseases = (List<Disease>) data.getSerializableExtra(QuizActivity.DISEASES_LIST_KEY);
                adapter.addCheckedDisease(diseases);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void checkKindOrFinish(int kind) {
        if (kind == ChooseKindActivity.PATIENT_CODE ||
            kind == ChooseKindActivity.THERAPIST_CODE) {
            return;
        }

        finish();
    }

    private void assignViews() {
        usernameView = findViewById(R.id.username_edittext);
        emailView = findViewById(R.id.email_edittext);
        nameView = findViewById(R.id.name_edittext);
        passView = findViewById(R.id.password_edittext);
        confirmPassView = findViewById(R.id.confirm_password_edittext);
        descriptionView = findViewById(R.id.descriere_edittext);
        addressView = findViewById(R.id.adresa_cabinet_edittext);

        registrationProgress = findViewById(R.id.register_progress);
        illnessesProgress = findViewById(R.id.illnesses_progress);
        recyclerView = findViewById(R.id.disease_list);

        countrySpinner = findViewById(R.id.country_spinner);
        citySpinner = findViewById(R.id.city_spinner);
    }

    private void setUpSpinners() {
        ArrayAdapter<CharSequence> countryAdapter = ArrayAdapter.createFromResource(
                this, R.array.array_countries, android.R.layout.simple_spinner_item
        );

        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(countryAdapter);

        ArrayAdapter<CharSequence> citiesAdapter = ArrayAdapter.createFromResource(
                this, R.array.array_cities, android.R.layout.simple_spinner_item
        );

        citiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(citiesAdapter);
    }

    private void setUpRecycler() {
        adapter = new DiseaseAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();
        retrieveIllnessesFromDB();
    }

    private void retrieveIllnessesFromDB() {
        if (FIRST_TIME_RETRIEVE_ILLNESSES) {
            FIRST_TIME_RETRIEVE_ILLNESSES = false;
            getSupportLoaderManager().initLoader(ILLNESSES_LOADER_ID, null,
                    new IllnessesLoaderCallbacks(this, illnessesProgress, adapter, recyclerView)).forceLoad();
        } else {
            getSupportLoaderManager().restartLoader(ILLNESSES_LOADER_ID, null,
                    new IllnessesLoaderCallbacks(this, illnessesProgress, adapter, recyclerView)).forceLoad();
        }
    }

    private void setUpGUI(int code) {
        EditText cabinetAddress = findViewById(R.id.adresa_cabinet_edittext);
        EditText description = findViewById(R.id.descriere_edittext);
        TextView hint = findViewById(R.id.illnesses_list_hint_textview);
        Button quizButton = findViewById(R.id.goToQuiz_button);

        if (code == ChooseKindActivity.PATIENT_CODE) {
            setTitle(R.string.title_patient);
            hint.setText(R.string.hint_choose_illnesses);
            cabinetAddress.setVisibility(View.GONE);
            description.setVisibility(View.GONE);
            quizButton.setVisibility(View.VISIBLE);
        } else {
            setTitle(R.string.title_therapist);
            hint.setText(R.string.hint_choose_specializations);
            cabinetAddress.setVisibility(View.VISIBLE);
            description.setVisibility(View.VISIBLE);
            quizButton.setVisibility(View.GONE);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 6;
    }

    private User packData() {
        User user = null;
        String name = nameView.getText().toString();
        String username = usernameView.getText().toString();
        String email = emailView.getText().toString();
        String country = countrySpinner.getSelectedItem().toString();
        String city = citySpinner.getSelectedItem().toString();

        if (kindCode == ChooseKindActivity.PATIENT_CODE) {
            user = new Patient(0, name, username, email, country, city);
            ((Patient) user).setDiseases(adapter.getCheckedDiseases());
        } else if (kindCode == ChooseKindActivity.THERAPIST_CODE) {
            Therapist t = new Therapist(0, name, username, email, country, city);
            t.setCabinetAddress(addressView.getText().toString());
            t.setDescription(descriptionView.getText().toString());
            t.setSpecializations(adapter.getCheckedDiseases());
            user = t;
        }


        return user;
    }

    public boolean checkEntries(User user) {
        String pass = passView.getText().toString();
        String confirmPass = confirmPassView.getText().toString();

        usernameView.setError(null);
        emailView.setError(null);
        passView.setError(null);
        confirmPassView.setError(null);
        nameView.setError(null);


        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            usernameView.setError(getResources().getString(R.string.error_invalid_username));
            usernameView.requestFocus();
            return false;
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            nameView.setError(getResources().getString(R.string.error_invalid_name));
            nameView.requestFocus();
            return false;
        }

        if (!isEmailValid(user.getEmail())) {
            emailView.setError(getResources().getString(R.string.error_invalid_username));
            emailView.requestFocus();
            return false;
        }

        if (!isPasswordValid(pass)) {
            passView.setError(getResources().getString(R.string.error_invalid_password));
            passView.requestFocus();
            return false;
        }

        if (!pass.equals(confirmPass)) {
            confirmPassView.setError(getResources().getString(R.string.error_mismached_passwords));
            confirmPassView.requestFocus();
            return false;
        }

        if (!adapter.existSelectedItems()) {
            Toast.makeText(this, R.string.error_unchecked_illnesses, Toast.LENGTH_LONG).show();
            recyclerView.requestFocus();
            return false;
        }

        if (countrySpinner.getSelectedItem().toString().equals("-")) {
            Toast.makeText(this, R.string.error_unselected_country, Toast.LENGTH_LONG).show();
            countrySpinner.requestFocus();
            return false;
        }

        if (citySpinner.getSelectedItem().toString().equals("-")) {
            Toast.makeText(this, R.string.error_unselected_city, Toast.LENGTH_LONG).show();
            citySpinner.requestFocus();
            return false;
        }

        return true;
    }

    public void attemptRegister(View view) {
        if (!checkEntries(packData())) {
            return;
        }

        if (FIRST_TIME_LOADED) {
            FIRST_TIME_LOADED = false;
            getSupportLoaderManager().initLoader(REGISTER_LOADER_ID, null, this).forceLoad();
        } else {
            getSupportLoaderManager().restartLoader(REGISTER_LOADER_ID, null, this).forceLoad();
        }
    }

    @NonNull
    @Override
    public Loader<User> onCreateLoader(int i, @Nullable Bundle bundle) {
        if (i == REGISTER_LOADER_ID) {
            registrationProgress.setVisibility(View.VISIBLE);
            return new RegisterTask(this, packData(), passView.getText().toString(), publishHandler, this);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<User> loader, User user) {
        registrationProgress.setVisibility(View.GONE);

        if (user != null) {
            Intent intent = new Intent();
            intent.putExtra(USER_KEY, user);
            setResult(LoginActivity.REGISTRATION_SUCCESSFUL_CODE, intent);
            finish();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<User> loader) {
        registrationProgress.setVisibility(View.GONE);
    }

    public void forwardToQuizEvent(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.title_quiz_activity)
                .setMessage(R.string.instructions_forward_to_quiz)
                .setPositiveButton(R.string.message_positive, (dialog, which) -> {
                    dialog.dismiss();
                    Intent intent = new Intent(RegisterActivity.this, QuizActivity.class);
                    intent.putExtra(QuizActivity.GET_DISEASES_KEY, true);
                    startActivityForResult(intent, DISEASE_LIST_REQUEST_CODE);
                })
                .setNegativeButton(R.string.message_negative, (dialog, which) -> {
                    dialog.dismiss();
                })
                .setIcon(android.R.drawable.ic_dialog_info);

        builder.show();
    }

    private static class RegisterTask extends AsyncTaskLoader<User> {
        private User user;
        private String pass;
        private ErrorPublisherHandler publisherHandler;
        private RegisterActivity activity;

        RegisterTask(@NonNull Context context, User user, String pass, ErrorPublisherHandler publisherHandler, RegisterActivity activity) {
            super(context);
            this.user = user;
            this.pass = pass;
            this.publisherHandler = publisherHandler;
            this.activity = activity;
        }

        @Nullable
        @Override
        public User loadInBackground() {
            User u;
            try {
                u = UserController.getInstance().registerUser(user, pass);
            } catch (EmailInUseException e) {
                Message m = publisherHandler.obtainMessage();
                m.obj = activity.emailView;
                m.arg1 = R.string.error_email_in_use;
                publisherHandler.sendMessage(m);
                u = null;
            } catch (UsernameInUseException e) {
                Message m = publisherHandler.obtainMessage();
                m.obj = activity.usernameView;
                m.arg1 = R.string.error_username_in_use;
                publisherHandler.sendMessage(m);
                u = null;
            }

            return u;
        }

    }

    private static class RetrieveIllnessesTask extends AsyncTaskLoader<List<Disease>> {

        public RetrieveIllnessesTask(@NonNull Context context) {
            super(context);
        }

        @Nullable
        @Override
        public List<Disease> loadInBackground() {
            List<Disease> list = IllnessesController.getInstance().findAllIllnesses();
            return list != null ? list : Collections.emptyList();
        }
    }

    private static class IllnessesLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Disease>> {
        private Context context;
        private ProgressBar progressBar;
        private DiseaseAdapter adapter;
        private RecyclerView recyclerView;

        private IllnessesLoaderCallbacks(Context context, ProgressBar progressBar, DiseaseAdapter adapter, RecyclerView recyclerView) {
            this.context = context;
            this.progressBar = progressBar;
            this.adapter = adapter;
            this.recyclerView = recyclerView;
        }


        @NonNull
        @Override
        public Loader<List<Disease>> onCreateLoader(int i, @Nullable Bundle bundle) {
            if (i == ILLNESSES_LOADER_ID) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                return new RetrieveIllnessesTask(context);
            }
            return null;
        }

        @Override
        public void onLoadFinished(@NonNull Loader<List<Disease>> loader, List<Disease> strings) {
            progressBar.setVisibility(View.GONE);
            adapter.addDiseases(strings);
            adapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<List<Disease>> loader) {

        }
    }

    private static class ErrorPublisherHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (!(msg.obj instanceof EditText)) {
                return;
            }

            ((EditText) msg.obj).setError(ResourceController.getInstance().getResource().getString(msg.arg1));
            ((EditText) msg.obj).requestFocus();
        }
    }
}
