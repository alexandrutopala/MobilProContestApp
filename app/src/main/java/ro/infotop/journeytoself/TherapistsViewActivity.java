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

import java.util.ArrayList;
import java.util.List;

import ro.infotop.journeytoself.adapter.TherapistAdapter;
import ro.infotop.journeytoself.listener.RecyclerItemClickListener;
import ro.infotop.journeytoself.model.userModel.Patient;
import ro.infotop.journeytoself.model.userModel.Therapist;
import ro.infotop.journeytoself.model.userModel.User;
import ro.infotop.journeytoself.service.UserController;

public class TherapistsViewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Therapist>> {
    private static final int RETURN_FROM_THERAPIST_PROFILE_CODE = 1;
    private static final int THERAPIST_LOADER_ID = 1;
    private boolean FIRST_RUN_FLAG = true;

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TherapistAdapter adapter;

    private Patient user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_therapists_view);

        User user = UserController.getInstance().getLoggedUser();
        if (!(user instanceof Patient)) {
            finish();
            return;
        }
        this.user = (Patient) user;

        assignView();
        setUpRecycler();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startLoading();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RETURN_FROM_THERAPIST_PROFILE_CODE) {
            if (resultCode == RESULT_CANCELED) {
                user.setTherapist(null);
            } else if (resultCode == RESULT_OK) {
                // TODO: go to therapist profile and meetings
                finish();
            }
        }
    }

    private void assignView() {
        recyclerView = findViewById(R.id.therapist_recyclerview);
        progressBar = findViewById(R.id.progress_load_therapists);
    }

    private void setUpRecycler() {
        adapter = new TherapistAdapter(this, new ArrayList<>());
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener(){

                    @Override
                    public boolean onItemClick(View view, int position) {
                        user.setTherapist(adapter.get(position));
                        Intent intent = new Intent(TherapistsViewActivity.this, ProfileActivity.class);
                        intent.putExtra(ProfileActivity.ACTION_VIEW_THERAPIST_PROFILE, true);
                        startActivityForResult(intent, RETURN_FROM_THERAPIST_PROFILE_CODE);
                        return true;
                    }

                    @Override
                    public boolean onLongItemClick(View view, int position) {
                        return true;
                    }
                })
        );
    }

    private void startLoading() {
        if (FIRST_RUN_FLAG) {
            FIRST_RUN_FLAG = false;
            getSupportLoaderManager().initLoader(THERAPIST_LOADER_ID, null, this).forceLoad();
        } else {
            getSupportLoaderManager().restartLoader(THERAPIST_LOADER_ID, null, this).forceLoad();
        }
    }

    @NonNull
    @Override
    public Loader<List<Therapist>> onCreateLoader(int i, @Nullable Bundle bundle) {
        if (i == THERAPIST_LOADER_ID) {
            progressBar.setVisibility(View.VISIBLE);
            return new FindTherapistsTask(this, user);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Therapist>> loader, List<Therapist> therapists) {
        progressBar.setVisibility(View.GONE);

        adapter.clear();
        adapter.addTherapists(therapists);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Therapist>> loader) {

    }


    private static class FindTherapistsTask extends AsyncTaskLoader<List<Therapist>> {
        private Patient user;

        public FindTherapistsTask(@NonNull Context context, Patient user) {
            super(context);
            this.user = user;
        }

        @Nullable
        @Override
        public List<Therapist> loadInBackground() {
            return UserController.getInstance().findTherapistsForPatient(user);
        }
    }
}
