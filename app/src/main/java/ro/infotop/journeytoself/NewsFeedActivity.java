package ro.infotop.journeytoself;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import ro.infotop.journeytoself.adapter.NewsAdapter;
import ro.infotop.journeytoself.commons.Disease;
import ro.infotop.journeytoself.listener.RecyclerItemClickListener;
import ro.infotop.journeytoself.model.diaryModel.Post;
import ro.infotop.journeytoself.model.newsModel.Article;
import ro.infotop.journeytoself.model.newsModel.News;
import ro.infotop.journeytoself.model.userModel.Patient;
import ro.infotop.journeytoself.model.userModel.Therapist;
import ro.infotop.journeytoself.model.userModel.User;
import ro.infotop.journeytoself.service.NewsController;
import ro.infotop.journeytoself.service.UserController;

public class NewsFeedActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Void> {
    public static final String TAG = NewsFeedActivity.class.getSimpleName();
    public static final int REFRESH_ARTICLES_REQUEST_CODE = 3;
    private static final int ARTICLES_LOADER = 1;
    private static final int ADD_ARTICLE_LOADER = 2;
    private static final int ADD_ARTICLE_REQUEST_CODE = 2;
    private static boolean FLAG_REFRESH_ARTICLES;

    private boolean FIRST_TIME_LOADING_FLAG = true;
    private boolean ARTICLES_ARE_LOADING_FLAG = false;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Article> articles = new ArrayList<>();
    private NewsAdapter adapter;
    private ProgressBar progressLoadingArticles;
    private SwipeRefreshLayout refreshLayout;
    private FloatingActionButton fab;

    private long[] articlesIds;
    private Handler refreshListHandler = new RefreshHandler();

    /////////////////// Activity callbacks
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);
        setTitle(R.string.title_activity_news_feed);

        //////////////////// Gui setup
        recyclerView = findViewById(R.id.recyclerviewFeed);
        progressLoadingArticles = findViewById(R.id.progress_load_articles);
        NestedScrollView scrollView = findViewById(R.id.scrollview_news);
        refreshLayout = findViewById(R.id.articles_refresher);
        fab = findViewById(R.id.add_article_fab);

/////////////////////////////// long class implementations
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (nestedScrollView, i, i1, i2, i3) -> {
            if (recyclerView.getChildCount() == 0) {
                return;
            }
            int bottomChild = recyclerView.getChildAt(recyclerView.getChildCount() - 1).getBottom();
            int dim = nestedScrollView.getHeight() + nestedScrollView.getScrollY();

            if (!ARTICLES_ARE_LOADING_FLAG && bottomChild <= dim) {
                 // start other loader after the current one has finished
                startLoader(null);
                return;
            }

        });

        refreshLayout.setOnRefreshListener(this::refreshArticles);



        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener(){

                    @Override
                    public boolean onItemClick(View view, int position) {
                        long articleId = adapter.getItem(position).getId();
                        Intent intent = new Intent(NewsFeedActivity.this, ViewArticleActivity.class);
                        intent.putExtra("articleId", articleId);
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

        ///////////////////// Recycler setup

        layoutManager = new LinearLayoutManager(this);
        adapter = new NewsAdapter(new ArrayList<>(), this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        adapter.notifyDataSetChanged();

        overridePendingTransition(R.transition.activity_fade_in, R.transition.activity_slide_out);

        refreshArticles();

//        articlesIds = null;
//        if (savedInstanceState != null) {
//            articlesIds = savedInstanceState.getLongArray("articlesIds");
//        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (UserController.getInstance().getLoggedUser() instanceof Therapist) {
            fab.show();
        } else {
            fab.hide();
        }

        if (FLAG_REFRESH_ARTICLES) {
            FLAG_REFRESH_ARTICLES = false;
            refreshArticles();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //refreshArticles();
    }

    @Override
    public void onBackPressed() {
        // TODO: close application
        System.exit(0);
        //finishAndRemoveTask();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //long[] articlesIds = adapter.getArticlesIds();
        //outState.putLongArray("articlesIds", articlesIds);
    }

    public void addArticle(View view) {
        Intent intent = new Intent(this, EditPostActivity.class);
        startActivityForResult(intent, ADD_ARTICLE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_ARTICLE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Post post = (Post) data.getSerializableExtra(DiaryActivity.POST_KEY);
                long matcherCode = data.getLongExtra(EditPostActivity.MATCHER_CODE_KEY, 0);
                Article art = NewsController.getInstance().parseArticle(
                        post,
                        UserController.getInstance().getLoggedUser()
                );
                art.setMatcherCode(matcherCode);
                new SaveArticleTaskLoader(
                        this,
                        art,
                        post.getContent(),
                        adapter,
                        progressLoadingArticles
                ).execute();
            }
        }

    }

    public static void requestRefreshArticles() {
        FLAG_REFRESH_ARTICLES = true;
    }

    /////////////////// Decoupled methods
    private void refreshArticles() {
        NewsFeedActivity.this.getSupportLoaderManager().destroyLoader(ARTICLES_LOADER);
        ARTICLES_ARE_LOADING_FLAG = false;
        FIRST_TIME_LOADING_FLAG = true;
        adapter.clear();
        new Thread(() -> {
            User user = UserController.getInstance().getLoggedUser();

            if (user instanceof Patient) {
                NewsController.getInstance().initAndUpdate(((Patient) user).getMatcherCode());
            } else {
                NewsController.getInstance().initAndUpdate();
            }
            refreshListHandler.sendMessage(refreshListHandler.obtainMessage());
        }).start();
    }

    private void goToLoginActivity() {
        if (UserController.getInstance().getLoggedUser() != null) {
            Intent i = new Intent(this, ProfileActivity.class);
            i.putExtra(RegisterActivity.USER_KEY, UserController.getInstance().getLoggedUser());
            startActivityForResult(i, REFRESH_ARTICLES_REQUEST_CODE);
        } else {
            Intent i = new Intent(this, LoginActivity.class);
            startActivityForResult(i, REFRESH_ARTICLES_REQUEST_CODE);
        }
    }

    /////////////////// Menus

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news_feed_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem_access_account :
                goToLoginActivity();
                break;
            case R.id.menuitem_articles_refresh :
                refreshArticles();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    /////////////////// Loaders
    private void startLoader(long[] articlesIds) {

        Bundle bundle = new Bundle();
        bundle.putLongArray("articlesIds", articlesIds);

        ARTICLES_ARE_LOADING_FLAG = true;
        LoaderManager loaderManager = getSupportLoaderManager();
        if (FIRST_TIME_LOADING_FLAG) {
            FIRST_TIME_LOADING_FLAG = false;
            Loader<Void> loader = loaderManager.initLoader(ARTICLES_LOADER, bundle, this);
            loader.forceLoad();
        } else {
            Loader<Void> loader = loaderManager.restartLoader(ARTICLES_LOADER, bundle, this);
            loader.forceLoad();
        }
    }

    @NonNull
    @Override
    public Loader<Void> onCreateLoader(int i, @Nullable Bundle bundle) {
        if (i == ARTICLES_LOADER) {
            long[] articlesIds = bundle.getLongArray("articlesIds");

            progressLoadingArticles.setVisibility(View.VISIBLE);
            return new ArticlesLoader(this, adapter, articlesIds);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Void> loader, Void aVoid) {
        ARTICLES_ARE_LOADING_FLAG = false;
        progressLoadingArticles.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Void> loader) {

    }

    private static class ArticlesLoader extends AsyncTaskLoader<Void> {
        private NewsAdapter adapter;
        private long[] articlesIds;

        public ArticlesLoader(Context context, @NonNull NewsAdapter adapter) {
            super(context);
            this.adapter = adapter;
        }

        public ArticlesLoader(@NonNull Context context, @NonNull NewsAdapter adapter, long[] articlesIds) {
            this(context, adapter);
            this.articlesIds = articlesIds;
        }

        @Nullable
        @Override
        public Void loadInBackground() {
            if (!NewsController.getInstance().existMoreArticles()) {
                return null;
            }
            News bulk;

            if (articlesIds != null) {
                // don't load other news, restore previous
                adapter.clear();
                bulk = NewsController.getInstance().findArticlesById(articlesIds);
            } else {
                User user = UserController.getInstance().getLoggedUser();
                if (user instanceof Patient) {
                    bulk = NewsController.getInstance().getNextNews(((Patient) user).getMatcherCode());
                } else {
                    bulk = NewsController.getInstance().getNextNews();
                }
            }



            if (bulk != null && bulk.getArticles() != null) {
                adapter.addArticles(bulk.getArticles());
            }
            return null;
        }

        @Override
        protected void onStopLoading() {
            super.onStopLoading();
            adapter.notifyDataSetChanged();
        }
    }

    private static class SaveArticleTaskLoader extends AsyncTask<Void, Void, Void> {

        private Article article;
        private String content;
        private NewsAdapter adapter;
        @SuppressLint("StaticFieldLeak")
        private ProgressBar progressBar;
        @SuppressLint("StaticFieldLeak")
        private NewsFeedActivity context;

        private SaveArticleTaskLoader(NewsFeedActivity context, Article article, String content, NewsAdapter adapter, ProgressBar progressBar) {
            this.context = context;
            this.article = article;
            this.content = content;
            this.adapter = adapter;
            this.progressBar = progressBar;
        }

        @Override
        protected void onPreExecute() {
            adapter.clear();
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Nullable
        @Override
        public Void doInBackground(Void... voids) {
            NewsController.getInstance().persistArticle(article, content);
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            progressBar.setVisibility(View.GONE);
            adapter.addFirstArticle(article);
            context.refreshArticles();
        }
    }

    /////////////////// Handlers
    private class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            startLoader(null);
            refreshLayout.setRefreshing(false);
        }
    }
}
