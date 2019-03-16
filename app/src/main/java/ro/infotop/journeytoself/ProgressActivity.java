package ro.infotop.journeytoself;

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
import android.widget.Button;
import android.widget.ProgressBar;

import ro.infotop.journeytoself.model.userModel.User;
import ro.infotop.journeytoself.service.ProgressController;
import ro.infotop.journeytoself.service.UserController;
import ro.infotop.journeytoself.utils.Callback;

public class ProgressActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Boolean[]> {
    private static final int PROGRESS_CHECKER_ID = 1;

    private boolean FIRST_LOAD_FLAG = true;
    private boolean quizCompleted = false;

    private Button questionsButton;
    private Button diaryButton;
    private Button statisticsButton;
    private ProgressBar progressBar;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        user = UserController.getInstance().getLoggedUser();
        if (user == null) {
            finish();
            return;
        }

        assignView();
        startLoading();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startLoading();
    }

    private void assignView() {
        questionsButton = findViewById(R.id.questions_button);
        diaryButton = findViewById(R.id.diary_button);
        statisticsButton = findViewById(R.id.statistics_button);
        progressBar = findViewById(R.id.progress_check_progress);
    }

    private void disableViews() {
        diaryButton.setEnabled(false);
        questionsButton.setEnabled(false);
        statisticsButton.setEnabled(false);
    }

    private void enableViews() {
        diaryButton.setEnabled(true);
        questionsButton.setEnabled(true);
        statisticsButton.setEnabled(true);
    }

    private void startLoading() {
        if (FIRST_LOAD_FLAG) {
            FIRST_LOAD_FLAG = false;
            getSupportLoaderManager().initLoader(PROGRESS_CHECKER_ID, null, this).forceLoad();
        } else {
            getSupportLoaderManager().restartLoader(PROGRESS_CHECKER_ID, null, this).forceLoad();
        }
    }

    public void goToQuestions(View view) {
        // TODO: implements this
        if (quizCompleted) {
            new RetrieveQuizResultTask(user, this, Callback.empty()).execute(user);
            return;
        }

        Intent intent = new Intent(this, QuizActivity.class);
        startActivity(intent);
    }

    public void goToDiary(View view) {
        // TODO: implements this
        Intent intent = new Intent(this, DiaryActivity.class);
        startActivity(intent);
    }

    public void goToStatistics(View view) {
        // TODO: implements this
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

    @NonNull
    @Override
    public Loader<Boolean[]> onCreateLoader(int i, @Nullable Bundle bundle) {
        if (i == PROGRESS_CHECKER_ID) {
            progressBar.setVisibility(View.VISIBLE);
            disableViews();
            return new CheckProgressTask(this, user);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Boolean[]> loader, Boolean[] bools) {
        progressBar.setVisibility(View.INVISIBLE);
        enableViews();

        if (bools[0]) { // quiz completed
            questionsButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    android.R.drawable.ic_menu_help, 0, R.drawable.outline_check_black_24, 0
            );
        } else {
            questionsButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    android.R.drawable.ic_menu_help, 0, 0, 0
            );
        }
        quizCompleted = bools[0];

        if (bools[1]) { // diary note made
            diaryButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    android.R.drawable.ic_menu_agenda, 0, R.drawable.outline_check_black_24, 0
            );
        } else {
            diaryButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    android.R.drawable.ic_menu_agenda, 0, 0, 0
            );
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Boolean[]> loader) {
    }

    private static class CheckProgressTask extends AsyncTaskLoader<Boolean[]> {
        private User user;

        public CheckProgressTask(@NonNull Context context, User user) {
            super(context);
            this.user = user;
        }

        @Nullable
        @Override
        public Boolean[] loadInBackground() {
            boolean quizCompleted = ProgressController.getInstance().isTodayQuizCompleted(user);
            boolean diaryCompleted = ProgressController.getInstance().hadPostedInDiaryToday(user);
            return new Boolean[]{
                    quizCompleted,
                    diaryCompleted
            };
        }
    }

    private static class RetrieveQuizResultTask extends AsyncTask<User, Void, Double> {
        private User user;
        private Context context;
        private Callback onFinish;
        private ProgressDialog progressDialog;

        private RetrieveQuizResultTask(User user, Context context, Callback onFinish) {
            this.user = user;
            this.context = context;
            this.onFinish = onFinish;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(
                    context,
                    context.getString(R.string.title_retrieve_quiz_result),
                    "",
                    true,
                    false
            );
        }

        @Override
        protected Double doInBackground(User... users) {
            Double result = ProgressController.getInstance().getTodayQuizResult(user);
            return result != null ? result : Double.NaN;
        }

        @Override
        protected void onPostExecute(Double aDouble) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                Intent intent = new Intent(context, ResultActivity.class);
                intent.putExtra(QuizActivity.SCORE_KEY, aDouble);
                context.startActivity(intent);
                onFinish.call();
            }

        }
    }
}
