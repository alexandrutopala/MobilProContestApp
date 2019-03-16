package ro.infotop.journeytoself.service;

import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.infotop.journeytoself.model.diaryModel.Post;
import ro.infotop.journeytoself.model.newsModel.Article;
import ro.infotop.journeytoself.model.userModel.User;
import ro.infotop.journeytoself.repository.PostsRepo;

public class PostsController {
    private static final int INIT_BULK = 7; // 20
    private static final int BULK = 1; // 10
    private int givenPosts = 0;
    private Date lastUpdate;
    private List<Post> bulkPosts;
    private PostsRepo postsRepo;

    private static Map<User, PostsController> registry = new HashMap<>();

    private PostsController(User user) {
        postsRepo = new PostsRepo(user, true);
    }

    public static PostsController getInstance(User user) {
        if (registry.get(user) == null) {
            synchronized (PostsController.class) {
                if (registry.get(user) == null) {
                    registry.put(user, new PostsController(user));
                }
            }
        }
        return registry.get(user);
    }

    public void initAndUpdate() {
        lastUpdate = Calendar.getInstance().getTime();
        bulkPosts = postsRepo.getPosts(lastUpdate, 0, INIT_BULK);
        givenPosts = 0; //Math.min(INIT_BULK, bulkPosts.size());
    }

    @Nullable
    public List<Post> getNextPosts() {
        List<Post> postsToGive = bulkPosts;
        givenPosts += postsToGive.size();//Math.min(BULK, bulkPosts.size());
        bulkPosts = postsRepo.getPosts(lastUpdate, givenPosts, givenPosts + BULK);

        if (postsToGive.isEmpty()) {
            return null;
        }

        return postsToGive;
    }

    public int getPostsCount() {
        return postsRepo.getPostsCount();
    }

    public boolean existMoreArticles() {
        return getPostsCount() > givenPosts;
    }

    public Post findPostsById(long articleId) {
        return postsRepo.findPostById(articleId);
    }


    public List<Post> findPostsById(long[] postsIds) {
        return postsRepo.findPostsById(postsIds);
    }

    public boolean persistPost(Post post) {
        return postsRepo.persistPost(post);
    }

    public boolean hadPosted(User who, Date when) {
        return postsRepo.hadPosted(who, when);
    }
}
