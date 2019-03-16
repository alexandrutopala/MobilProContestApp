package ro.infotop.journeytoself;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ro.infotop.journeytoself.adapter.PostsAdapter;
import ro.infotop.journeytoself.listener.RecyclerItemClickListener;
import ro.infotop.journeytoself.model.diaryModel.Post;
import ro.infotop.journeytoself.model.userModel.Patient;
import ro.infotop.journeytoself.model.userModel.Therapist;
import ro.infotop.journeytoself.model.userModel.User;
import ro.infotop.journeytoself.service.PostsController;
import ro.infotop.journeytoself.service.UserController;

public class DiaryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Void> {

    public static final String TAG = DiaryActivity.class.getSimpleName();
    private static final int POSTS_LOADER = 1;
    private static final String FIRST_RUN_KEY = "isThFirstRun";
    public static final String POST_ID = "postIdr";
    public static final String POST_KEY = "postKeyds";
    public static final int ADD_POST_REQUEST_CODE = 32;

    private boolean FIRST_TIME_LOADING_FLAG = true;
    private boolean POSTS_ARE_LOADING_FLAG = false;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Post> posts = new ArrayList<>();
    private PostsAdapter adapter;
    private ProgressBar progressLoadingArticles;

    private User user;
    private FloatingActionButton addFab;
    private long[] postsIds;

    /////////////////// Activity callbacks
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        setTitle(R.string.title_activity_diary);

///////////////////////////// Retrieve user and activate the corresponding mode
        user = UserController.getInstance().getLoggedUser();
        if (user == null) {
            finish();
            return;
        }


////////////////////////////// find and assign views
        addFab = findViewById(R.id.add_post_fab);
        recyclerView = findViewById(R.id.recyclerviewPosts);
        progressLoadingArticles = findViewById(R.id.progress_load_posts);
        NestedScrollView scrollView = findViewById(R.id.scrollview_posts);

        if (user instanceof Patient) {
            startPatientMode();
        } else if (user instanceof Therapist) {
            Therapist t = (Therapist) user;
            if (t.getKeyPatient() == null) {
                // terapeutul nu are asociata o cheie (instanta de pacient) cu care sa vizualizeze acest continut
                finish();
                return;
            } else {
                user = t.getKeyPatient();
                startPatientModeFromTherapistPerspective();
            }
        }




/////////////////////////////// long class implementations
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (nestedScrollView, i, i1, i2, i3) -> {
            int bottomChild = recyclerView.getChildAt(recyclerView.getChildCount() - 1).getBottom();
            int dim = nestedScrollView.getHeight() + nestedScrollView.getScrollY();

            if (!POSTS_ARE_LOADING_FLAG && bottomChild <= dim) {
                // start other loader after the current one has finished
                startLoader(null);
                return;
            }

        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener(){

                    @Override
                    public boolean onItemClick(View view, int position) {
                        long postId = adapter.getItem(position).getId();
                        Intent intent = new Intent(DiaryActivity.this, EditPostActivity.class);
                        intent.putExtra(POST_ID, postId);
                        startActivity(intent);
                        return true;
                    }

                    @Override
                    public boolean onLongItemClick(View view, int position) {
                        return true;
                    }
                })
        );
/////////////////////////////////// end

        layoutManager = new LinearLayoutManager(this);
        adapter = new PostsAdapter(new ArrayList<>(), this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        adapter.notifyDataSetChanged();

        overridePendingTransition(R.transition.activity_fade_in, R.transition.activity_slide_out);

        postsIds = null;
        if (savedInstanceState != null) {
            postsIds = savedInstanceState.getLongArray("postsIds");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startLoader(postsIds);
    }

    public void addPost(View view) {
        Intent intent = new Intent(this, EditPostActivity.class);
        startActivityForResult(intent, ADD_POST_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == ADD_POST_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Post p = (Post) data.getSerializableExtra(POST_KEY);
                adapter.addFirst(p);
                adapter.notifyDataSetChanged();
            }
        }
    }

    ////////////////// Activity's modes

    private void startPatientModeFromTherapistPerspective() {
        addFab.hide();
    }

    private void startPatientMode() {
        addFab.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        long[] articlesIds = adapter.getPostsIds();
        outState.putLongArray("postsIds", articlesIds);
    }

    /////////////////// Loaders
    private void startLoader(long[] articlesIds) {


        Bundle bundle = new Bundle();
        bundle.putLongArray("postsIds", articlesIds);
        bundle.putBoolean(FIRST_RUN_KEY, FIRST_TIME_LOADING_FLAG);

        POSTS_ARE_LOADING_FLAG = true;
        LoaderManager loaderManager = getSupportLoaderManager();
        if (FIRST_TIME_LOADING_FLAG) {
            FIRST_TIME_LOADING_FLAG = false;
            Loader<Void> loader = loaderManager.initLoader(POSTS_LOADER, bundle, this);
            loader.forceLoad();
        } else {
            Loader<Void> loader = loaderManager.restartLoader(POSTS_LOADER, bundle, this);
            loader.forceLoad();
        }
    }

    @NonNull
    @Override
    public Loader<Void> onCreateLoader(int i, @Nullable Bundle bundle) {
        if (i == POSTS_LOADER) {
            long[] postsIds = bundle.getLongArray("postsIds");
            boolean firstRun = bundle.getBoolean(FIRST_RUN_KEY, true);

            progressLoadingArticles.setVisibility(View.VISIBLE);
            return new DiaryActivity.PostsLoader(this, adapter, postsIds, user, firstRun);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Void> loader, Void aVoid) {
        POSTS_ARE_LOADING_FLAG = false;
        progressLoadingArticles.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Void> loader) {

    }

    private static class PostsLoader extends AsyncTaskLoader<Void> {
        private PostsAdapter adapter;
        private long[] postsIds;
        private User user;
        private boolean firstRun;

        public PostsLoader(Context context, @NonNull PostsAdapter adapter, User user, boolean firstRun) {
            super(context);
            this.adapter = adapter;
            this.user = user;
            this.firstRun = firstRun;
        }

        public PostsLoader(@NonNull Context context, @NonNull PostsAdapter adapter, long[] postsIds, User user, boolean firstRun) {
            this(context, adapter, user, firstRun);
            this.postsIds = postsIds;
        }

        @Nullable
        @Override
        public Void loadInBackground() {
            if (firstRun) {
                PostsController.getInstance(user).initAndUpdate();
            }


            if (!PostsController.getInstance(user).existMoreArticles()) {
                return null;
            }
            // TODO: resolve logic
            List<Post> bulk;

            if (postsIds != null) {
                // don't load other news, restore previous
                adapter.clear();
                bulk = PostsController.getInstance(user).findPostsById(postsIds);
            } else {
                bulk = PostsController.getInstance(user).getNextPosts();
            }

            if (bulk != null) {
                adapter.addPosts(bulk);
            }
            return null;
        }

        @Override
        protected void onStopLoading() {
            super.onStopLoading();
            adapter.notifyDataSetChanged();
        }
    }
}


