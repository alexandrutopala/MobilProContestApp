package ro.infotop.journeytoself;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ro.infotop.journeytoself.adapter.PatientAdapter;
import ro.infotop.journeytoself.listener.RecyclerItemClickListener;
import ro.infotop.journeytoself.model.therapyModel.TherapyRequest;
import ro.infotop.journeytoself.model.userModel.Patient;
import ro.infotop.journeytoself.model.userModel.Therapist;
import ro.infotop.journeytoself.model.userModel.User;
import ro.infotop.journeytoself.service.RequestController;
import ro.infotop.journeytoself.service.UserController;

public class PatientsViewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Map<Patient, TherapyRequest>> {
    private static final int PATIENTS_LOADER_ID = 1;
    private static final int INVALID_INDEX = -1;
    private static final int RETURN_FROM_PROFILE_ACTIVITY_REQUEST_CODE = 1;

    private boolean FIRST_LOAD_FLAG = true;
    private int lastTappedItemIndex = INVALID_INDEX;

    private Therapist user;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyListTextView;

    private PatientAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_view);

        checkUser();
        assignViews();
        setUpRecycler();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startLoading();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RETURN_FROM_PROFILE_ACTIVITY_REQUEST_CODE) {
            user.setKeyPatient(null);
        }
    }

    private void checkUser() {
        User user = UserController.getInstance().getLoggedUser();
        if (user == null) {
            finish();
            return;
        }

        if (!(user instanceof Therapist)) {
            finish();
            return;
        }
        this.user = (Therapist) user;
    }

    private void assignViews() {
        recyclerView = findViewById(R.id.patients_recyclerview);
        progressBar = findViewById(R.id.progress_load_patients);
        emptyListTextView = findViewById(R.id.empty_list_message_textview);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener(){

                    @Override
                    public boolean onItemClick(View view, int position) {
                        lastTappedItemIndex = position;
                        return false;
                    }

                    @Override
                    public boolean onLongItemClick(View view, int position) {
                        return false;
                    }
                })
        );
    }

    private void setUpRecycler() {
        adapter = new PatientAdapter(new HashMap<>(), this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
        if (FIRST_LOAD_FLAG) {
            FIRST_LOAD_FLAG = false;
            getSupportLoaderManager().initLoader(PATIENTS_LOADER_ID, null, this).forceLoad();
        } else {
            getSupportLoaderManager().restartLoader(PATIENTS_LOADER_ID, null, this).forceLoad();
        }
    }

    public void viewProfileEvent(View view) {
        if (lastTappedItemIndex != INVALID_INDEX) {
            Patient p = adapter.getPatient(lastTappedItemIndex);

            user.setKeyPatient(p);
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivityForResult(intent, RETURN_FROM_PROFILE_ACTIVITY_REQUEST_CODE);
        }

    }

    public void acceptPatientEvent(View view) {
        if (lastTappedItemIndex != INVALID_INDEX) {
            Patient p = adapter.getPatient(lastTappedItemIndex);
            TherapyRequest tr = adapter.getRequest(p);

            tr.setStatus(TherapyRequest.Status.ACCEPTED);
            adapter.notifyDataSetChanged();
            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(
                    () -> RequestController.getInstance(PatientsViewActivity.this)
                            .updateRequestStatus(p, user, TherapyRequest.Status.ACCEPTED, true)
            );
            service.shutdown();
        }
    }

    public void declinePatientEvent(View view) {
        if (lastTappedItemIndex != INVALID_INDEX) {
            Patient p = adapter.getPatient(lastTappedItemIndex);
            TherapyRequest tr = adapter.getRequest(p);

            tr.setStatus(TherapyRequest.Status.REJECTED);
            adapter.remove(p);
            adapter.notifyDataSetChanged();
            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(
                    () -> RequestController.getInstance(PatientsViewActivity.this)
                            .updateRequestStatus(p, user, TherapyRequest.Status.REJECTED, true)
            );
            service.shutdown();
        }
    }

    public void cancelTherapyEvent(View view) {
        if (lastTappedItemIndex != INVALID_INDEX) {
            Patient p = adapter.getPatient(lastTappedItemIndex);
            TherapyRequest tr = adapter.getRequest(p);

            tr.setStatus(TherapyRequest.Status.REJECTED);
            adapter.remove(p);
            adapter.notifyDataSetChanged();
            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(
                    () -> RequestController.getInstance(PatientsViewActivity.this)
                            .updateRequestStatus(p, user, TherapyRequest.Status.REJECTED, true)
            );
            service.shutdown();
        }
    }

    @NonNull
    @Override
    public Loader<Map<Patient, TherapyRequest>> onCreateLoader(int i, @Nullable Bundle bundle) {
        if (i == PATIENTS_LOADER_ID) {
            return new LoadPatientsTask(this, user);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Map<Patient, TherapyRequest>> loader, Map<Patient, TherapyRequest> patientTherapyRequestMap) {
        progressBar.setVisibility(View.GONE);
        adapter.clear();
        adapter.addAll(patientTherapyRequestMap);
        adapter.notifyDataSetChanged();

        if (adapter.getItemCount() != 0) {
            emptyListTextView.setVisibility(View.GONE);
        } else {
            emptyListTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Map<Patient, TherapyRequest>> loader) {

    }


    private static class LoadPatientsTask extends AsyncTaskLoader<Map<Patient, TherapyRequest>> {

        private Therapist user;

        public LoadPatientsTask(@NonNull Context context, Therapist user) {
            super(context);
            this.user = user;
        }

        @NotNull
        @Override
        public Map<Patient, TherapyRequest> loadInBackground() {
            Map<Patient, TherapyRequest> map = new HashMap<>();
            List<Patient> patients = UserController.getInstance().findPatientsByTherapist(user);

            if (patients == null) {
                return map;
            }

            for (Patient p : patients) {
                TherapyRequest request = RequestController.getInstance(getContext()).findSingleRequest(p, user);
                if (request.getStatus() != TherapyRequest.Status.REJECTED) {
                    map.put(p, request);
                }
            }

            return map;
        }
    }


}
