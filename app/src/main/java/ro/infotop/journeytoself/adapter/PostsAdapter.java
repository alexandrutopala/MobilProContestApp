package ro.infotop.journeytoself.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ro.infotop.journeytoself.DiaryActivity;
import ro.infotop.journeytoself.R;
import ro.infotop.journeytoself.model.diaryModel.Post;
import ro.infotop.journeytoself.model.newsModel.Article;

public class PostsAdapter extends Adapter<PostsAdapter.PostHolder> {

    private List<Post> posts;
    private Context context;

    public PostsAdapter(List<Post> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.item_post, viewGroup, false
        );
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder postHolder, int i) {
        Post p = posts.get(i);
        String month = new SimpleDateFormat("MMM").format(p.getDate());
        String day = new SimpleDateFormat("dd").format(p.getDate());

        postHolder.postDescriptionTextView.setText(p.getContent());
        postHolder.postTitleTextView.setText(p.getTitle());
        postHolder.dayTextView.setText(day);
        postHolder.monthTextView.setText(month);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public Post getItem(int index) {
        return posts.get(index);
    }

    public long[] getPostsIds() {
        long[] ids = new long[posts.size()];
        int index = 0;
        for (Post p : posts) {
            ids[index++] = p.getId();
        }
        return ids;
    }

    public void clear() {
        posts.clear();
    }

    public void addPosts(List<Post> posts) {
        this.posts.addAll(posts);
    }

    public void addFirst(Post p) {
        posts.add(0, p);
    }

    static class PostHolder extends RecyclerView.ViewHolder {
        TextView monthTextView;
        TextView dayTextView;
        TextView postTitleTextView;
        TextView postDescriptionTextView;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            monthTextView = itemView.findViewById(R.id.month_textview);
            dayTextView = itemView.findViewById(R.id.day_textview);
            postTitleTextView = itemView.findViewById(R.id.title_post_textview);
            postDescriptionTextView = itemView.findViewById(R.id.descriere_post_textview);

        }
    }
}
