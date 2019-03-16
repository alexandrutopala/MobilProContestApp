package ro.infotop.journeytoself;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ro.infotop.journeytoself.listener.DiseaseCallback;
import ro.infotop.journeytoself.model.diaryModel.Post;
import ro.infotop.journeytoself.model.userModel.Therapist;
import ro.infotop.journeytoself.model.userModel.User;
import ro.infotop.journeytoself.service.PostsController;
import ro.infotop.journeytoself.service.ProgressController;
import ro.infotop.journeytoself.service.UserController;
import ro.infotop.journeytoself.utils.AsyncImageLoader;

public class EditPostActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Post>, DiseaseCallback {
    public static final String MATCHER_CODE_KEY = "matcherCodeKeyFromEditPost";
    private static final int POST_LOADER_ID = 1;
    private final static long INVALID_POST_ID = -1;
    private final static int PICK_IMAGE_REQUEST_CODE = 21;

    private User user;
    private long postId;
    private Post post;

    private TextView dateTextView;
    private EditText titleEditText;
    private EditText contentEditText;
    private ProgressBar loadPostProgress;
    private ImageView imageView;

    private boolean FIRST_LOAD_FLAG = true;
    private boolean EDITING_MODE_FLAG = false;

    private AsyncImageLoader imageLoader;
    private SavePostTask savePostTask;
    private String imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        setTitle(R.string.title_diary_post);

        postId = getIntent().getLongExtra(DiaryActivity.POST_ID, INVALID_POST_ID);

        //////////// check user
        user = UserController.getInstance().getLoggedUser();
        if (user == null) {
            finish();
            return;
        }

        if (user instanceof Therapist) {
            Therapist t = (Therapist) user;
            if (t.getKeyPatient() != null) {
                user = t.getKeyPatient();
            }
        }

        assignViews();

        if (postId == INVALID_POST_ID) {
            startEditingMode();
            post = new Post();
            post.setDate(Calendar.getInstance().getTime());
            populateGUI(post);
        } else {
            startViewingMode();
            startLoading();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (imageLoader != null) {
            imageLoader.cancel(true);
        }
    }


    @Override
    public void onBackPressed() {
        if (savePostTask != null) {
            Toast.makeText(this, R.string.message_unfinished_task, Toast.LENGTH_SHORT).show();
            return;
        }
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private MenuItem saveMenuItem;
    private MenuItem editMenuItem;
    private MenuItem discardMenuItem;
    private MenuItem addPhotoMenuItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = new MenuInflater(this);
        mi.inflate(R.menu.menu_edit_post, menu);

        saveMenuItem = menu.findItem(R.id.action_save_post);
        saveMenuItem.setVisible(EDITING_MODE_FLAG);
        editMenuItem = menu.findItem(R.id.action_edit_post);
        editMenuItem.setVisible(!EDITING_MODE_FLAG);
        discardMenuItem = menu.findItem(R.id.action_discard_post);
        discardMenuItem.setVisible(EDITING_MODE_FLAG);
        addPhotoMenuItem = menu.findItem(R.id.action_add_photo);
        addPhotoMenuItem.setVisible(EDITING_MODE_FLAG);

        if (UserController.getInstance().getLoggedUser() instanceof Therapist) {
            Therapist t = (Therapist) UserController.getInstance().getLoggedUser();
            if (t.getKeyPatient() != null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (savePostTask != null) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_save_post :
                if (user instanceof Therapist) {
                    Therapist t = (Therapist) user;
                    if (t.getKeyPatient() == null) {
                        showSelectDiseasesDialog();
                        break;
                    }
                }
                savePost(null);
                break;
            case R.id.action_edit_post :
                editMenuItem.setVisible(false);
                saveMenuItem.setVisible(true);
                discardMenuItem.setVisible(true);
                addPhotoMenuItem.setVisible(true);
                startEditingMode();
                break;
            case R.id.action_discard_post :
                if (postId == INVALID_POST_ID) {
                    setResult(RESULT_CANCELED);
                    finish();
                }

                saveMenuItem.setVisible(false);
                editMenuItem.setVisible(true);
                discardMenuItem.setVisible(false);
                addPhotoMenuItem.setVisible(false);
                populateGUI(post);
                startViewingMode();
                break;
            case R.id.action_add_photo :
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            imageUri = data.getData().toString();
            if (imageUri != null) {
                imageLoader = new AsyncImageLoader(this, imageView, Uri.parse(imageUri), loadPostProgress, () -> imageLoader = null);
                imageLoader.execute();
            }

        }
    }

    private void showSelectDiseasesDialog() {
        SelectDiseasesDialog dialog = new SelectDiseasesDialog();
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), SelectDiseasesDialog.TAG);
    }

    private void savePost(@Nullable Long matcherCode) {
        post.setTitle(titleEditText.getText().toString());
        post.setContent(contentEditText.getText().toString());
        post.setDate(Calendar.getInstance().getTime());
        post.setImageUri(imageUri);

        savePostTask = new SavePostTask(
                user,
                loadPostProgress,
                this,
                matcherCode);

        savePostTask.execute(post);

        saveMenuItem.setVisible(false);
        discardMenuItem.setVisible(false);
        addPhotoMenuItem.setVisible(false);
        editMenuItem.setVisible(true);
        startViewingMode();
    }

    private void assignViews() {
        dateTextView = findViewById(R.id.post_date_textview);
        titleEditText = findViewById(R.id.post_title_edittext);
        contentEditText = findViewById(R.id.note_edittext);
        loadPostProgress = findViewById(R.id.progress_load_post);
        imageView = findViewById(R.id.attached_photo_imageview);

        contentEditText.setOnTouchListener((v, event) -> {
            if (contentEditText.hasFocus()) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP : v.getParent().requestDisallowInterceptTouchEvent(false); break;
                }
            }
            return false;
        });
    }

    private void startEditingMode() {
        titleEditText.setEnabled(true);
        contentEditText.setEnabled(true);
        contentEditText.requestFocus();
        EDITING_MODE_FLAG = true;
    }

    private void startViewingMode() {
        titleEditText.setEnabled(false);
        contentEditText.setEnabled(false);
        EDITING_MODE_FLAG = false;
    }

    private void startLoading() {
        if (FIRST_LOAD_FLAG) {
            FIRST_LOAD_FLAG = false;
            getSupportLoaderManager().initLoader(POST_LOADER_ID, null, this).forceLoad();
        } else {
            getSupportLoaderManager().restartLoader(POST_LOADER_ID, null, this).forceLoad();
        }
    }

    private void populateGUI(@NotNull Post post) {
        dateTextView.setText(
                new SimpleDateFormat("dd.MM.yyyy HH:mm").format(post.getDate())
        );

        titleEditText.setText(post.getTitle());
        contentEditText.setText(post.getContent());
        if (post.getImageUri() != null) {
            if (imageLoader != null) {
                imageLoader.cancel(true);
            }
            imageLoader = new AsyncImageLoader(this, imageView, Uri.parse(post.getImageUri()), loadPostProgress, () -> imageLoader = null);
            imageLoader.execute();
        }
    }

    @Override
    public void call(long matcherCode, int resultCode) {
        if (resultCode == RESULT_OK) {
            savePost(matcherCode);
        }
    }

    @NonNull
    @Override
    public Loader<Post> onCreateLoader(int i, @Nullable Bundle bundle) {
        if (i == POST_LOADER_ID) {
            loadPostProgress.setVisibility(View.VISIBLE);
            return new LoadPostTask(this, postId, user);
        }

        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Post> loader, Post aPost) {
        loadPostProgress.setVisibility(View.GONE);
        this.post = aPost;
        if (this.post == null) {
            this.post = new Post();
        }

        populateGUI(post);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Post> loader) {

    }



    private static class LoadPostTask extends AsyncTaskLoader<Post> {
        private Long postId;
        private User user;

        public LoadPostTask(@NonNull Context context, Long postId, User user) {
            super(context);
            this.postId = postId;
            this.user = user;
        }

        @Nullable
        @Override
        public Post loadInBackground() {
            return PostsController.getInstance(user).findPostsById(postId);
        }
    }

    private static class SavePostTask extends AsyncTask<Post, Void, Boolean> {
        private User user;
        @SuppressLint("StaticFieldLeak")
        private ProgressBar progress;
        @SuppressLint("StaticFieldLeak")
        private EditPostActivity context;
        private Post post;
        @Nullable
        private Long matcherCode;


        private SavePostTask(User user, ProgressBar progress, EditPostActivity context, @Nullable Long matcherCode) {
            this.user = user;
            this.progress = progress;
            this.context = context;
            this.matcherCode = matcherCode;
        }

        @Override
        protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);

        }

        @Override
        protected Boolean doInBackground(Post... posts) {
            Post post = posts[0];
            this.post = post;
            if (UserController.getInstance().getLoggedUser() instanceof Therapist) {
                return true;
            }

            ProgressController.getInstance().markTodayUserPostedInDiary(user);
            return PostsController.getInstance(user).persistPost(post);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progress.setVisibility(View.GONE);
            context.savePostTask = null;
            if (aBoolean) {
                Toast.makeText(context, R.string.message_post_saved, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra(DiaryActivity.POST_KEY, post);
                if (matcherCode != null) {
                    intent.putExtra(MATCHER_CODE_KEY, matcherCode);
                }

                context.setResult(Activity.RESULT_OK, intent);

            } else {
                Toast.makeText(context, R.string.message_post_unsaved, Toast.LENGTH_SHORT).show();
                context.setResult(Activity.CONTEXT_RESTRICTED);
            }

            if (context.postId == INVALID_POST_ID) {
                context.finish();
            }
        }
    }

}
