package ro.infotop.journeytoself;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ro.infotop.journeytoself.model.userModel.Therapist;
import ro.infotop.journeytoself.model.userModel.User;
import ro.infotop.journeytoself.service.ProgressController;
import ro.infotop.journeytoself.service.UserController;

public class StatisticsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Map<String, Double>> {
    private static final int PROGRESS_LOADER_ID = 1;
    private boolean FIRST_LOAD_FLAG = true;

    private User user;

    private BarChart chart;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        checkUser();
        assignView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startLoading();
    }

    private void checkUser() {
        user = UserController.getInstance().getLoggedUser();

        if (user == null) {
            finish();
            return;
        }

        if (user instanceof Therapist) {
            Therapist t = (Therapist) user;
            if (t.getKeyPatient() == null) {
                finish();
                return;
            }
            user = t.getKeyPatient();
        }
    }

    private void assignView() {
        chart = findViewById(R.id.progress_barChart);
        progressBar = findViewById(R.id.progress_load_chart);
    }

    private void setUpChart(Map<String, Double> stringDoubleMap) {
        List<BarEntry> barEntries = new ArrayList<>();
        List<String> dates = new ArrayList<>();

        int index = 0;
        for (String date : stringDoubleMap.keySet()) {
            Double score = stringDoubleMap.get(date);
            barEntries.add(new BarEntry((float) ((double) (score != null ? score : 0.0)), index++));
            dates.add(date);
        }

        BarDataSet dataSet = new BarDataSet(barEntries, getString(R.string.x_axis_label_dates));
        BarData barData = new BarData(dates, dataSet);

        chart.setData(barData);
        chart.setDescription("");
        chart.postInvalidate();
    }

    private void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
        if (FIRST_LOAD_FLAG) {
            FIRST_LOAD_FLAG = false;
            getSupportLoaderManager().initLoader(PROGRESS_LOADER_ID, null, this).forceLoad();
        } else {
            getSupportLoaderManager().restartLoader(PROGRESS_LOADER_ID, null, this).forceLoad();
        }
    }

    @NonNull
    @Override
    public Loader<Map<String, Double>> onCreateLoader(int i, @Nullable Bundle bundle) {
        if (i == PROGRESS_LOADER_ID) {
            return new LoadProgressTask(this, user);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Map<String, Double>> loader, Map<String, Double> stringDoubleMap) {
        setUpChart(stringDoubleMap);
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void onLoaderReset(@NonNull Loader<Map<String, Double>> loader) {

    }

    public static class LoadProgressTask extends AsyncTaskLoader<Map<String, Double>> {
        private User user;

        public LoadProgressTask(@NonNull Context context, User user) {
            super(context);
            this.user = user;
        }

        @Nullable
        @Override
        public Map<String, Double> loadInBackground() {
            return ProgressController.getInstance().getAllScores(user);
        }
    }
}
