package ro.infotop.journeytoself.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import ro.infotop.journeytoself.R;
import ro.infotop.journeytoself.model.newsModel.Article;
import ro.infotop.journeytoself.utils.DateUtils;
import ro.infotop.journeytoself.utils.NewsApiUtils;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsHolder> {

    private List<Article> articles;
    private Context context;

    public NewsAdapter(List<Article> articles, Context context) {
        this.articles = articles;
        this.context = context;
    }

    public void addArticles(List<Article> arts) {
        articles.addAll(arts);
    }

    public void addArticle(Article art) {
        articles.add(art);
    }

    public void clear() {
        articles.clear();
    }

    public synchronized long[] getArticlesIds() {
        long[] ids = new long[articles.size()];

        for (int i = 0; i < ids.length; i++) {
            ids[i] = articles.get(i).getId();
        }

        return ids;
    }

    public Article getItem(int pos) {
        return articles.get(pos);
    }

    @NonNull
    @Override
    public NewsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_news, viewGroup, false);
        return new NewsHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsHolder newsHolder, int i) {
        Article art = articles.get(i);

        // load image using Glide library
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(NewsApiUtils.getDrawableColor(i));
        requestOptions.error(NewsApiUtils.getDrawableColor(i));
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();

        Glide.with(context)
                .load(art.getUrlToImage())
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        newsHolder.progress.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        newsHolder.progress.setVisibility(View.GONE);
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(newsHolder.image);

        String time = DateUtils.parseDateAndTime(
                DateUtils.parseStringDateAndTime(art.getPublishAt()),
                "HH:mm"
        );

        String date = DateUtils.parseDateAndTime(
                DateUtils.parseStringDateAndTime(art.getPublishAt()),
                "dd MMM yyyy"
        );


        newsHolder.title.setText(art.getTitle());
        newsHolder.publishedAt.setText(date);
        newsHolder.source.setText(art.getSource().getName());
        newsHolder.description.setText(art.getDescription());
        newsHolder.time.setText(" \u2022 " + time);
        newsHolder.author.setText(art.getAuthor());

    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public void addFirstArticle(Article article) {
        articles.add(0, article);
    }

    class NewsHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView author;
        TextView description;
        TextView source;
        TextView publishedAt;
        TextView time;
        ImageView image;
        ProgressBar progress;
        public NewsHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textview_title);
            author = itemView.findViewById(R.id.textview_author);
            description = itemView.findViewById(R.id.textview_description);
            source = itemView.findViewById(R.id.textview_source);
            publishedAt = itemView.findViewById(R.id.textview_publishedAt);
            time = itemView.findViewById(R.id.textview_time);
            image = itemView.findViewById(R.id.img);
            progress = itemView.findViewById(R.id.progress_load_photo);

        }
    }

}
