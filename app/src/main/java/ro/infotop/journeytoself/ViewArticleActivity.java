package ro.infotop.journeytoself;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import ro.infotop.journeytoself.commons.Disease;
import ro.infotop.journeytoself.model.newsModel.Article;
import ro.infotop.journeytoself.service.NewsController;
import ro.infotop.journeytoself.service.PostsController;
import ro.infotop.journeytoself.utils.NewsApiUtils;

public class ViewArticleActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Object[]> {
    private static final int CONTENT_LOADER_ID = 1;

    private boolean FIRST_LOAD_FLAG = true;
    private ProgressBar progressBar;
    private ProgressBar progressImage;
    private long articleId;
    private TextView articleContentView;
    private ImageView articleImage;
    private CollapsingToolbarLayout toolbarLayout;

    private TextView titleTextView;
    private TextView publishedAtTextView;
    private TextView authorTextView;
    private TextView sourceTextView;
    private TextView tagsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_article);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assignView();

        articleId = getIntent().getLongExtra("articleId", -1);

        if (articleId == -1) {
            finish();
            Toast.makeText(this, R.string.error_invalid_article_id, Toast.LENGTH_SHORT).show();
            return;
        }

        if (FIRST_LOAD_FLAG) {
            FIRST_LOAD_FLAG = false;
            getSupportLoaderManager().initLoader(CONTENT_LOADER_ID, null, this).forceLoad();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        overridePendingTransition(R.transition.activity_fade_in, R.transition.activity_slide_out);
    }

    private void assignView() {
        toolbarLayout = findViewById(R.id.toolbar_layout);
        articleContentView = findViewById(R.id.textview_article_content);
        progressBar = findViewById(R.id.progress_load_article);
        articleImage = findViewById(R.id.image_article);
        progressImage = findViewById(R.id.progress_load_article_image);

        titleTextView = findViewById(R.id.article_title_textview);
        publishedAtTextView = findViewById(R.id.textview_publishedAt1);
        authorTextView = findViewById(R.id.textview_author1);
        sourceTextView = findViewById(R.id.textview_source1);
        tagsTextView = findViewById(R.id.textview_tags);
    }

    @NonNull
    @Override
    public Loader<Object[]> onCreateLoader(int i, @Nullable Bundle bundle) {
        if (i == CONTENT_LOADER_ID) {
            progressBar.setVisibility(View.VISIBLE);
            return new ContentLoader(this, articleId);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object[]> loader, Object[] objects) {
        Article article = (Article) objects[0];
        String articleContent = (String) objects[1];

        if (article != null) {
            loadPhoto(article.getUrlToImage());
            toolbarLayout.setTitle(article.getTitle());
            populateUI(article);
        }

        articleContentView.setText(
                articleContent != null ? articleContent : getResources().getString(R.string.error_unavailable_content)
        );

        progressBar.setVisibility(View.GONE);
    }

    private void populateUI(Article art) {
        titleTextView.setText(art.getTitle());
        sourceTextView.setText(art.getUrl());
        authorTextView.setText(art.getAuthor());
        publishedAtTextView.setText(art.getPublishAt());
        tagsTextView.setText(processTags(art.getMatcherCode()));
    }

    private String processTags(long matcherCode) {
        List<Disease> diseases = Disease.generateDiseasesList(matcherCode);

        if (diseases.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        int size = diseases.size();

        for (int i = 0; i < size - 1; i++) {
            sb.append(diseases.get(i));
            sb.append(" \u2022 ");
        }
        sb.append( diseases.get(size-1) );
        return sb.toString();
    }

    private void loadPhoto(String urlToImage) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(NewsApiUtils.getRandomDrawableColor());
        requestOptions.error(NewsApiUtils.getRandomDrawableColor());
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();

        Glide.with(this)
                .load(urlToImage)
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressImage.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressImage.setVisibility(View.GONE);
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(articleImage);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object[]> loader) {
    }

    private static class ContentLoader extends AsyncTaskLoader<Object[]> {
        private long articleId;

        private ContentLoader(Context context, long articleId) {
            super(context);
            this.articleId = articleId;
        }

        @Nullable
        @Override
        public Object[] loadInBackground() {
            Article article = NewsController.getInstance().findArticleById(articleId);
            String articleContent = NewsController.getInstance().getArticleContent(articleId);
            return new Object[]{article, articleContent};
        }

    }
}
