package ro.infotop.journeytoself;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import ro.infotop.journeytoself.commons.Disease;
import ro.infotop.journeytoself.model.quizModel.Question;
import ro.infotop.journeytoself.model.userModel.Patient;
import ro.infotop.journeytoself.model.userModel.Therapist;
import ro.infotop.journeytoself.model.userModel.User;
import ro.infotop.journeytoself.service.IllnessesController;
import ro.infotop.journeytoself.service.ProgressController;
import ro.infotop.journeytoself.service.QuestionsController;
import ro.infotop.journeytoself.service.UserController;
import ro.infotop.journeytoself.utils.Callback;
import ro.infotop.journeytoself.utils.MetricsUtil;
import ro.infotop.journeytoself.utils.ParametricCallback;

/*
    Logica salvarii: nu pot fi mai mult de 1 intrebare blank. cand se da back, blank-ul este eliminat.
    cand se incearca salvarea, se testeaza validitatea ultimei intrebari (posibil un blank), si daca se dovedeste a
    fi invalida, se sterge
 */

public class QuizActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Question>> {
    public final static String SCORE_KEY = "scorere";
    public final static String GET_DISEASES_KEY = "getDiseases";
    public final static String DISEASES_LIST_KEY = "diseaseList12";

    private static final int QUIZ_LOADER_ID = 1;
    private static final int INVALID_QUESTION_NUMBER = -1;

    private boolean ESTIMATE_DISEASE_FLAG;

    private Spinner categorySpinner;
    private EditText questionEditText;
    private LinearLayout answersLayout;
    private TextView paginationTextView;
    private Button prevButton;
    private Button nextButton;
    private ProgressBar progressBar;

    private List<Question> quiz;
    private User user;
    private int currentQuestion = INVALID_QUESTION_NUMBER;
    private Map<Integer, Button> selectedButtonForQuestion = new TreeMap<>();
    private Map<Integer, Integer> selectedButtonIndexForQuestion = new TreeMap<>();

    private boolean FIRST_LOAD_FLAG = true;
    private QuizSaveTask saveTask;
    private QuizEvaluateTask evaluateTask;
    private EstimateDiseasesTask estimateTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        setTitle(R.string.title_quiz_activity);

        ESTIMATE_DISEASE_FLAG = getIntent().getBooleanExtra(GET_DISEASES_KEY, false);


        user = UserController.getInstance().getLoggedUser();
        if (user == null && !ESTIMATE_DISEASE_FLAG) {
            finish();
            return;
        }

        assignViews();

        if (user instanceof Therapist) {
            startTherapistMode();
        } else {
            startPatientMode();
        }

        startLoading();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!(user instanceof Therapist)) {
            return false;
        }
        MenuInflater mi = new MenuInflater(this);
        mi.inflate(R.menu.menu_quiz_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_quiz :

                if (saveTask == null) {
                    ProgressDialog progressDialog = ProgressDialog.show(
                            this,
                            getString(R.string.action_saving_quiz),
                            getString(R.string.message_exponation_saving_quiz),
                            true,
                            false);
                    saveTask = new QuizSaveTask(() -> {
                        saveTask = null;
                        progressDialog.dismiss();
                        Toast.makeText(QuizActivity.this, R.string.message_saved, Toast.LENGTH_SHORT).show();
                    });


                    saveTask.execute(
                            isCurrentQuestionValid() ? quiz : quiz.subList(0, quiz.size() - 1)
                    );
                } else {
                    Toast.makeText(this, R.string.message_quiz_is_saving, Toast.LENGTH_SHORT).show();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (saveTask != null || evaluateTask != null || estimateTask != null) {
            return;
        }
        super.onBackPressed();
    }

    private void assignViews() {
        categorySpinner = findViewById(R.id.category_spinner);
        questionEditText = findViewById(R.id.question_edittext);
        answersLayout = findViewById(R.id.answers_layout);
        paginationTextView = findViewById(R.id.pagination_textview);
        prevButton = findViewById(R.id.prev_button);
        nextButton = findViewById(R.id.next_button);
        progressBar = findViewById(R.id.progress_load_quiz);

        questionEditText.setOnTouchListener((v, event) -> {
            if (questionEditText.hasFocus()) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP : v.getParent().requestDisallowInterceptTouchEvent(false); break;
                }
            }
            return false;
        });

        ArrayAdapter<Disease> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Disease.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                saveQuestion(currentQuestion);
                startLoading();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void goToQuestion(final int i) {
        if (i >= quiz.size() || i < 0) {
            return;
        }

        currentQuestion = i;
        paginationTextView.setText(new StringBuilder().append(currentQuestion + 1).append(" din ").append(quiz.size()).toString());

        if (i == 0) {
            prevButton.setVisibility(View.INVISIBLE);
        }

        if (i == quiz.size() - 1) {
            if (user instanceof Patient || ESTIMATE_DISEASE_FLAG) {
                nextButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0, 0, R.drawable.outline_check_black_24, 0
                );
            } else if (user instanceof Therapist) {
                nextButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0, 0, android.R.drawable.ic_input_add, 0
                );
            }
        }

        if (i < quiz.size() - 1) {
            nextButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0, 0, R.drawable.outline_arrow_forward_black_24, 0
            );
        }

        if (i > 0) {
            prevButton.setVisibility(View.VISIBLE);
        }

        questionEditText.setText(quiz.get(i).getQuestion());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(
                MetricsUtil.dpToPixels(this,10),
                MetricsUtil.dpToPixels(this,10),
                MetricsUtil.dpToPixels(this,10),
                MetricsUtil.dpToPixels(this,10)
        );


        answersLayout.removeAllViewsInLayout();

        if (user instanceof Patient || ESTIMATE_DISEASE_FLAG) {
            Integer selectedButton = selectedButtonIndexForQuestion.get(i);
            Set<String> answers = quiz.get(i).getAnswers().keySet();
            Button button;
            int j = 0;



            for (String ans : answers) {
                button = new Button(this);
                button.setVisibility(View.VISIBLE);
                button.setBackgroundResource(R.drawable.button_shape_oval);

                button.setEllipsize(TextUtils.TruncateAt.END);
                button.setSingleLine();
                button.setText(ans);
                if (selectedButton != null && j == selectedButton) {
                    ((GradientDrawable) button.getBackground()).setColor(
                            getResources().getColor(R.color.colorPrimaryLight)
                    );
                    selectedButtonForQuestion.put(i, button);
                } else {
                    //button.setBackgroundColor(getResources().getColor(R.color.white));
                    ((GradientDrawable) button.getBackground()).setColor(
                            getResources().getColor(R.color.white)
                    );
                }
                int J = j;
                button.setOnClickListener((v) -> optionSelected((Button) v, J, i));
                button.setLayoutParams(params);
                answersLayout.addView(button);
                j++;

            }
        } else if (user instanceof Therapist) {
            Map<String, Integer> answers = quiz.get(i).getAnswers();
            for (String a : answers.keySet()) {
                View answerLayout = LayoutInflater.from(this).inflate(R.layout.item_answer, answersLayout, false);
                ((EditText) answerLayout.findViewById(R.id.answer_edittext)).setText(a);
                ((EditText) answerLayout.findViewById(R.id.score_edittext)).setText(
                        String.valueOf(answers.get(a))
                );

                answerLayout.setLayoutParams(params);
                answersLayout.addView(answerLayout);
            }

            Button addButton = new Button(this);
            addButton.setLayoutParams(params);
            addButton.setBackgroundColor(
                    getResources().getColor(android.R.color.transparent)
            );
            addButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    android.R.drawable.ic_input_add, 0,0,0
            );
            addButton.setOnClickListener((v) -> addAnswerLayout(answersLayout, params));
            addButton.setText(R.string.add_answer_message);
            addButton.setGravity(Gravity.START);
            answersLayout.addView(addButton);

        }

    }

    private void addAnswerLayout(ViewGroup where, LinearLayout.LayoutParams params) {
        View answerLayout = LayoutInflater.from(this).inflate(R.layout.item_answer, answersLayout, false);
        answerLayout.setLayoutParams(params);
        where.addView(
                answerLayout,
                where.getChildCount() - 1);
    }

    private void optionSelected(Button view, int buttonIndex, int questionIndex) {
        Button selectedButton = selectedButtonForQuestion.get(questionIndex);
        if (selectedButton != null) {
            ((GradientDrawable) selectedButton.getBackground()).setColor(
                    getResources().getColor(R.color.white)
            );
        }

        ((GradientDrawable) view.getBackground()).setColor(
                getResources().getColor(R.color.colorPrimaryLight)
        );
        selectedButtonForQuestion.put(questionIndex, view);
        selectedButtonIndexForQuestion.put(questionIndex, buttonIndex);
        goToQuestion(currentQuestion + 1);
    }

    private void saveQuestion(int i) {
        if (quiz == null) {
            return;
        }
        if (i >= quiz.size() || i < 0) {
            return;
        }

        if (!(user instanceof Therapist)) {
            return;
        }

        if (!isCurrentQuestionValid()) {
            //Toast.makeText(this, R.string.error_cannot_save_unfilled_question, Toast.LENGTH_LONG).show();

            return;
        }

        LinkedHashMap<String, Integer> answers = new LinkedHashMap<>();
        for (int ansIndex = 0; ansIndex < answersLayout.getChildCount(); ansIndex++) {
            View answerLayout = answersLayout.getChildAt(ansIndex);

            if (!(answerLayout instanceof LinearLayout)) {
                continue;
            }

            String answer = ((EditText) answerLayout.findViewById(R.id.answer_edittext)).getText().toString();
            int score = Integer.parseInt(
                    ((EditText) answerLayout.findViewById(R.id.score_edittext)).getText().toString()
            );
            answers.put(answer, score);
        }
        quiz.get(i).setQuestion(questionEditText.getText().toString());
        quiz.get(i).setAnswers(answers);

    }

    private boolean isCurrentQuestionValid() {
        return !(questionEditText.getText() == null ||
               questionEditText.getText().toString().isEmpty() ||
               answersLayout.getChildCount() <= 1);

    }

    private void startTherapistMode() {
        categorySpinner.setVisibility(View.VISIBLE);
        questionEditText.setEnabled(true);
    }

    private void startPatientMode() {
        categorySpinner.setVisibility(View.GONE);
        questionEditText.setEnabled(false);
    }

    public void prevButtonClickEvent(View view) {
        if (!isCurrentQuestionValid()) {
            quiz.remove(currentQuestion);
        } else {
            saveQuestion(currentQuestion);
        }
        goToQuestion(currentQuestion - 1);
    }

    public void nextButtonClickEvent(View view) {
        if (currentQuestion == quiz.size() - 1) {
            if (user instanceof Patient) {
                evaluateTask = new QuizEvaluateTask(user, () -> evaluateTask = null, this, selectedButtonForQuestion);
                evaluateTask.execute(quiz);
                return;
            } else if (user instanceof Therapist) {
                if (!isCurrentQuestionValid()) {
                    Toast.makeText(this, R.string.error_connot_create_new_question, Toast.LENGTH_SHORT).show();
                    return;
                }

                Question q = new Question("", new LinkedHashMap<>(), (Disease) categorySpinner.getSelectedItem());
                quiz.add(q);
                goToQuestion(currentQuestion + 1);
            } else if (user == null && ESTIMATE_DISEASE_FLAG) {
                estimateTask = new EstimateDiseasesTask(
                        (result) -> {
                            Intent intent = new Intent();
                            intent.putExtra(DISEASES_LIST_KEY, result);
                            QuizActivity.this.setResult(RESULT_OK, intent);
                            estimateTask = null;
                            finish();
                        },
                        this,
                        selectedButtonForQuestion
                );
                estimateTask.execute(quiz);
            }

            return;
        }
        saveQuestion(currentQuestion);
        goToQuestion(currentQuestion + 1);
    }

    private void startLoading() {
        if (FIRST_LOAD_FLAG) {
            FIRST_LOAD_FLAG = false;
            getSupportLoaderManager().initLoader(QUIZ_LOADER_ID, null, this).forceLoad();
        } else {
            getSupportLoaderManager().restartLoader(QUIZ_LOADER_ID, null, this).forceLoad();
        }
    }

    @NonNull
    @Override
    public Loader<List<Question>> onCreateLoader(int i, @Nullable Bundle bundle) {
        if (i == QUIZ_LOADER_ID) {
            progressBar.setVisibility(View.VISIBLE);
            if (user instanceof Therapist) {
                return new QuizLoader(this, (Disease) categorySpinner.getSelectedItem());
            } else if (user instanceof Patient){
                List<Disease> diseases = ((Patient) user).getDiseases();
                return new QuizLoader(
                        this,
                        IllnessesController.getInstance().generateMatcherCode(diseases),
                        QuestionsController.MORE_QUESTIONS
                );
            } else if (user == null && ESTIMATE_DISEASE_FLAG) {
                return new QuizLoader(
                        this,
                        Disease.findMask(Disease.ALL_DISEASES),
                        QuestionsController.A_LOT_OF_QUESTIONS
                );
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Question>> loader, List<Question> questions) {
        progressBar.setVisibility(View.GONE);
        quiz = questions;
        if (quiz != null && !quiz.isEmpty()) {
            goToQuestion(0);
        } else {
            paginationTextView.setText(R.string.no_questions_message);
            answersLayout.removeAllViewsInLayout();
            questionEditText.setText("");
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Question>> loader) {
    }

    private static class QuizLoader extends AsyncTaskLoader<List<Question>> {
        private Disease category;
        private long matcherCode;
        private int howMany;

        public QuizLoader(@NonNull Context context, Disease category) {
            super(context);
            this.category = category;
        }

        public QuizLoader(@NonNull Context context, long matcherCode, int howMany) {
            super(context);
            this.matcherCode = matcherCode;
            this.howMany = howMany;
        }

        @Nullable
        @Override
        public List<Question> loadInBackground() {
            if (category != null) {
                return QuestionsController.getInstance().findAllQuestionsByCategory(category);
            } else {
                return QuestionsController.getInstance().findQuestionsByMatcherCode(matcherCode, howMany);
            }
        }
    }

    private static class QuizSaveTask extends AsyncTask<List<Question>, Void, Void> {
        private Callback onFinish;

        private QuizSaveTask(Callback onFinish) {
            this.onFinish = onFinish;
        }

        @Override
        protected Void doInBackground(List<Question>... lists) {
            List<Question> quiz = lists[0];

            for (Question q : quiz) {
                QuestionsController.getInstance().persistQuestion(q);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            onFinish.call();
        }
    }

    private static class QuizEvaluateTask extends AsyncTask<List<Question>, Void, Double> {
        private User user;
        private Callback onFinish;
        @SuppressLint("StaticFieldLeak")
        private QuizActivity context;
        private ProgressDialog progressDialog;
        private Map<Integer, Button> selectedButtons;

        private QuizEvaluateTask(User user, Callback onFinish, QuizActivity context, Map<Integer, Button> selectedButtons) {
            this.user = user;
            this.onFinish = onFinish;
            this.context = context;
            this.selectedButtons = selectedButtons;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(
                    context,
                    context.getString(R.string.message_saving_progress),
                    context.getString(R.string.message_exponation_saving_quiz),
                    true,
                    false
            );
        }

        @Override
        protected Double doInBackground(List<Question>... lists) {
            List<Question> quiz = lists[0];
            Map<String, String> qa = new HashMap<>();

            Integer score = 0;
            Integer maxScore = 0;
            for (Integer index : selectedButtons.keySet()) {
                String answer;
                answer = selectedButtons.get(index).getText().toString();
                quiz.get(index).setChosenAnswer(answer);

                qa.put(quiz.get(index).getQuestion(), answer);

                score += quiz.get(index).getAnswers().get(answer);
                maxScore += quiz.get(index).getMaximumScore();
            }

            Double result = 10.0 * score / maxScore;

            ProgressController.getInstance().persistTodayQuiz(qa, result, user);

            return result;
        }

        @Override
        protected void onPostExecute(Double result) {
            onFinish.call();
            progressDialog.dismiss();
            Intent intent = new Intent(context, ResultActivity.class);
            intent.putExtra(QuizActivity.SCORE_KEY, result);
            context.startActivity(intent);
            context.finish();
        }
    }

    private static class EstimateDiseasesTask extends AsyncTask<List<Question>, Void, ArrayList<Disease>> {
        private static final double INFERIOR_BOUND = 6.0;

        private ParametricCallback<ArrayList<Disease>> onFinish;
        @SuppressLint("StaticFieldLeak")
        private QuizActivity context;
        private ProgressDialog progressDialog;
        private Map<Integer, Button> selectedButtons;

        private EstimateDiseasesTask(ParametricCallback<ArrayList<Disease>> onFinish, QuizActivity context,
                                     Map<Integer, Button> selectedButtons) {
            this.onFinish = onFinish;
            this.context = context;
            this.selectedButtons = selectedButtons;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(
                    context,
                    context.getString(R.string.message_processing_answers),
                    "",
                    true,
                    false
            );
        }

        @SafeVarargs
        @Override
        protected final ArrayList<Disease> doInBackground(List<Question>... lists) {
            List<Question> quiz = lists[0];
            Map<Disease, Integer> scorePerDisease = new HashMap<>();
            Map<Disease, Integer> maxScorePerDisease = new HashMap<>();

            Question q;
            String answer;
            Integer score;
            Disease d;
            Integer diseaseScore;
            Integer maxScoreDisease;

            for (Integer index : selectedButtons.keySet()) {
                q = quiz.get(index);
                answer = selectedButtons.get(index).getText().toString();
                score = q.getAnswers().get(answer);
                d = q.getCategory();
                diseaseScore = scorePerDisease.get(d);

                scorePerDisease.put(d, (score != null ? score : 0) + (diseaseScore != null ? diseaseScore : 0 ));

                maxScoreDisease = maxScorePerDisease.get(d);
                maxScorePerDisease.put(d, (maxScoreDisease != null ? maxScoreDisease : 0) + q.getMaximumScore());
            }

            Map<Disease, Double> average = calculateAveragePerDisease(scorePerDisease, maxScorePerDisease);
            ArrayList<Disease> result = new ArrayList<>();

            for (Disease disease : average.keySet()) {
                if (average.get(disease) < INFERIOR_BOUND) {
                    result.add(disease);
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<Disease> result) {
            onFinish.call(result);
            progressDialog.dismiss();
        }

        private Map<Disease, Double> calculateAveragePerDisease(Map<Disease, Integer> scores, Map<Disease, Integer> maxScores) {
            Map<Disease, Double> average = new HashMap<>();

            Integer s;
            Integer ms;

            for (Disease d : scores.keySet()) {
                s = scores.get(d);
                ms = maxScores.get(d);

                Double result = 10.0 * s / ms;
                average.put(d, result);
            }
            return average;
        }

    }
}
