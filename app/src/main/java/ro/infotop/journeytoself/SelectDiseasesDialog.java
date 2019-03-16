package ro.infotop.journeytoself;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import ro.infotop.journeytoself.adapter.DiseaseAdapter;
import ro.infotop.journeytoself.commons.Disease;
import ro.infotop.journeytoself.listener.DiseaseCallback;
import ro.infotop.journeytoself.service.IllnessesController;
import ro.infotop.journeytoself.utils.Callback;

public class SelectDiseasesDialog extends DialogFragment {
    public static final String TAG = SelectDiseasesDialog.class.getSimpleName();
    private static final int ILLNESSES_LOADER_ID = 2;
    private boolean FIRST_TIME_RETRIEVE_ILLNESSES = true;

    private RecyclerView recyclerView;
    private ProgressBar illnessesProgress;
    private DiseaseAdapter adapter;

    private DiseaseCallback callback;
    private RetrieveIllnessAsyncTask illnessAsyncTask;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_diseases_list, container, false);

        assignView(layout);
        setUpRecycler();

        retrieveIllnessesFromDB();

        return layout;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof DiseaseCallback) {
            callback = (DiseaseCallback) context;
        }
    }

    private void assignView(View layout) {
        recyclerView = layout.findViewById(R.id.diseases_recyclerview);
        illnessesProgress = layout.findViewById(R.id.progress_load_diseases);
        Button dismissButton = layout.findViewById(R.id.dismiss_dialog_button1);
        Button acceptButton = layout.findViewById(R.id.ok_button);

        dismissButton.setOnClickListener(this::dismissDialogEvent);
        acceptButton.setOnClickListener(this::acceptEvent);
    }

    private void setUpRecycler() {
        adapter = new DiseaseAdapter(new ArrayList<>(), getContext());
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    private void dismissDialogEvent(View view) {
        dismiss();
    }

    private void acceptEvent(View view) {
        long matcherCode = Disease.generateMatchingCode(adapter.getCheckedDiseases());
        callback.call(matcherCode, Activity.RESULT_OK);
        dismiss();
    }


    private void retrieveIllnessesFromDB() {
        if (illnessAsyncTask == null) {
            illnessAsyncTask = new RetrieveIllnessAsyncTask(
                    illnessesProgress,
                    adapter,
                    recyclerView,
                    () -> illnessAsyncTask = null
            );
            illnessAsyncTask.execute();
        }
    }

    private static class RetrieveIllnessAsyncTask extends AsyncTask<Void, Void, List<Disease>> {

        @SuppressLint("StaticFieldLeak")
        private ProgressBar progressBar;
        private DiseaseAdapter adapter;
        @SuppressLint("StaticFieldLeak")
        private RecyclerView recyclerView;
        private Callback callback;

        private RetrieveIllnessAsyncTask(ProgressBar progressBar, DiseaseAdapter adapter, RecyclerView recyclerView, Callback callback) {
            this.progressBar = progressBar;
            this.adapter = adapter;
            this.recyclerView = recyclerView;
            this.callback = callback;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        @Override
        protected List<Disease> doInBackground(Void... voids) {
            return IllnessesController.getInstance().findAllIllnesses();
        }

        @Override
        protected void onPostExecute(List<Disease> diseases) {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.addDiseases(diseases);
            adapter.notifyDataSetChanged();

            if (callback != null) {
                callback.call();
            }
        }
    }

}
