package ro.infotop.journeytoself;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ChooseKindActivity extends AppCompatActivity {
    public final static String KIND_KEY = "kind";
    public final static int THERAPIST_CODE = 0;
    public final static int PATIENT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_kind);
    }

    public void registerTherapist(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra(KIND_KEY, THERAPIST_CODE);
        startActivityForResult(intent, LoginActivity.REGISTRATION_STATUS_REQUEST_CODE);
    }

    public void registerPatient(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra(KIND_KEY, PATIENT_CODE);
        startActivityForResult(intent, LoginActivity.REGISTRATION_STATUS_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        setResult(resultCode, data);
        finish();
    }
}
