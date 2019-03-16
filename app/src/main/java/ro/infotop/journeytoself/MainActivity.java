package ro.infotop.journeytoself;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ro.infotop.journeytoself.model.notificationModel.Notification;
import ro.infotop.journeytoself.service.NewsController;
import ro.infotop.journeytoself.service.NotificationCenter;
import ro.infotop.journeytoself.service.ResourceController;
import ro.infotop.journeytoself.utils.DateUtils;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Void> {
    private static final int CONTROLLER_INITIALIZATION_LOADER_ID = 1;
    private static final int REQUEST_PERMISSIONS_CODE = 1;
    private boolean CONTROLLERS_LOADED_FLAG = false;

    private Button startAppButton;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_load_application);
        startAppButton = findViewById(R.id.start_button);

        checkPermissions(null);
        //overridePendingTransition(R.transition.activity_fade_in, R.transition.activity_slide_out);
    }


    public void checkPermissions(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            List<String> permissionsNeeded = new ArrayList<>();
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            if (checkSelfPermission(Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.USE_FINGERPRINT);
            }

            if (!permissionsNeeded.isEmpty()) {
                String[] ps = new String[permissionsNeeded.size()];
                permissionsNeeded.toArray(ps);

                requestPermissions(ps, REQUEST_PERMISSIONS_CODE);
            } else {
                startLoading();
            }
        } else {
            startLoading();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_CODE && grantResults.length != 0) {
            // permissions[0] -> READ_EXTERNAL_PERMISSION
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLoading();
                startAppButton.setVisibility(View.GONE);
            } else {
                startAppButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private void startLoading() {
        LoaderManager loaderManager = getSupportLoaderManager();
        if (!CONTROLLERS_LOADED_FLAG) {
            loaderManager.initLoader(CONTROLLER_INITIALIZATION_LOADER_ID, null, this).forceLoad();
            CONTROLLERS_LOADED_FLAG = true;
        }
    }

    public void startApplication() {
        Intent intent = new Intent(this, NewsFeedActivity.class);
        startActivity(intent);
        finish();
    }

    @NonNull
    @Override
    public Loader<Void> onCreateLoader(int i, @Nullable Bundle bundle) {
        if (i == CONTROLLER_INITIALIZATION_LOADER_ID) {
            progressBar.setVisibility(View.VISIBLE);
            return new InitControllersLoader(this);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Void> loader, Void aVoid) {
        progressBar.setVisibility(View.GONE);
        startApplication();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Void> loader) {
    }

    private static class InitControllersLoader extends AsyncTaskLoader<Void> {


        public InitControllersLoader(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
        }

        @Nullable
        @Override
        public Void loadInBackground() {
            // DON'T CHANGE THE ORDER!!
            initResourceController();
            initNewsController();
            initNotificationCenter();
            return null;
        }

        private void initNotificationCenter() {
            // TODO: delete this after testing
            Notification n = new Notification(
                    DateUtils.parseStringDateAndTime("15.03.2019-19:23"),
                    "Cerere de terapie acceptata de: flori"
            );

            NotificationCenter.pushNotification(0, n);
        }

        private void initNewsController() {
            NewsController.getInstance().initAndUpdate();
        }

        private void initResourceController() {
            ResourceController rs = ResourceController.getInstance();
            rs.setContext(getContext());
            rs.setResource(getContext().getResources());
        }
    }
}
