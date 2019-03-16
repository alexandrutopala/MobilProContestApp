package ro.infotop.journeytoself.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import org.jetbrains.annotations.NotNull;

public class AsyncImageLoader extends AsyncTask<Void, Void, Void> {
    @SuppressLint("StaticFieldLeak")
    private Activity activity;
    @SuppressLint("StaticFieldLeak")
    private ImageView imageView;
    private Uri uri;
    @SuppressLint("StaticFieldLeak")
    private ProgressBar progressBar;
    private Callback onFinish;

    public AsyncImageLoader(@NotNull Activity activity,
                            @NotNull ImageView imageView,
                            @NotNull Uri uri,
                            @org.jetbrains.annotations.Nullable ProgressBar progressBar) {
        this.activity = activity;
        this.imageView = imageView;
        this.uri = uri;
        this.progressBar = progressBar;
    }

    public AsyncImageLoader(@NotNull Activity activity,
                            @NotNull ImageView imageView,
                            @NotNull Uri uri,
                            @org.jetbrains.annotations.Nullable ProgressBar progressBar,
                            @Nullable Callback onFinish) {
        this.activity = activity;
        this.imageView = imageView;
        this.uri = uri;
        this.progressBar = progressBar;
        this.onFinish = onFinish;
    }

    @Override
    protected void onPreExecute() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("CheckResult")
    @Override
    protected Void doInBackground(Void... voids) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(NewsApiUtils.getRandomDrawableColor());
        requestOptions.error(NewsApiUtils.getRandomDrawableColor());
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();

        RequestBuilder<Drawable> requestBuilder = Glide.with(activity)
                .load(uri)
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade());

        activity.runOnUiThread(() -> requestBuilder.into(imageView));
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        if (onFinish != null) {
            onFinish.call();
        }
    }
}
