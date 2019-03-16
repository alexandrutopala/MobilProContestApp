package ro.infotop.journeytoself;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class ResultActivity extends AppCompatActivity {

    private Double result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        result = getIntent().getDoubleExtra(QuizActivity.SCORE_KEY, Double.NaN);
        TextView resultView = findViewById(R.id.result_textview);
        resultView.setText(String.format(Locale.getDefault(), "%.1f", result));
    }

    public void backButtonEvent(View view) {
        finish();
    }
}
