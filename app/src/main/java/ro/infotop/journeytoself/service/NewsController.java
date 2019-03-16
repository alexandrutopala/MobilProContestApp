package ro.infotop.journeytoself.service;

import android.support.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;

import ro.infotop.journeytoself.model.diaryModel.Post;
import ro.infotop.journeytoself.model.newsModel.Article;
import ro.infotop.journeytoself.model.newsModel.News;
import ro.infotop.journeytoself.model.newsModel.Source;
import ro.infotop.journeytoself.model.userModel.User;
import ro.infotop.journeytoself.repository.NewsRepo;
import ro.infotop.journeytoself.utils.DateUtils;

public class NewsController {
    private static final int INIT_BULK = 4; // 20
    private static final int BULK = 2; // 10
    private static final int DESCRIPTION_LENGTH = 150;
    private static NewsController singleton;
    private int fromIndex = 0;
    private NewsRepo newsRepo;
    private Date lastUpdate;
    private News bulkNews;
    private boolean articlesEndFlag;

    private NewsController() {
        newsRepo = new NewsRepo(true);
    }

    public static NewsController getInstance() {
        if (singleton == null) {
            synchronized (PostsController.class) {
                if (singleton == null) {
                    singleton = new NewsController();
                }
            }
        }
        return singleton;
    }

    public void initAndUpdate(@Nullable Long matcherCode) {
        lastUpdate = Calendar.getInstance().getTime();
        bulkNews = newsRepo.getArticles(lastUpdate, 0, INIT_BULK, matcherCode);
        fromIndex = 0;//Math.min(INIT_BULK, bulkNews.getArticles().size());
        articlesEndFlag = false;
    }

    public void initAndUpdate() {
        initAndUpdate(null);
    }

    @Nullable
    public News getNextNews(@Nullable Long matcherCode) {
        News newsToGive = bulkNews;
        fromIndex = newsToGive.getFromIndex(); //Math.min(BULK, bulkNews.getArticles().size());
        bulkNews = newsRepo.getArticles(lastUpdate, fromIndex, BULK, matcherCode);

        if (newsToGive.getArticles().isEmpty()) {
            articlesEndFlag = true;
            return null;
        } else {
            articlesEndFlag = false;
        }

        return newsToGive;
    }

    @Nullable
    public News getNextNews() {
        return getNextNews(null);
    }

    public void persistArticle(Article art, String content) {
        newsRepo.persistArticle(art, content);
    }

    public int getTotalArticlesCount() {
        return newsRepo.getArticlesCount();
    }

    public boolean existMoreArticles() {
        // TODO: check flag strategy
        return getTotalArticlesCount() > fromIndex;
        //return !articlesEndFlag;
    }

    public Article findArticleById(long articleId) {
        return newsRepo.findArticleById(articleId);
    }

    public String getArticleContent(long articleId) {
        return newsRepo.getArticleContent(articleId);
    }

    public News findArticlesById(long[] articlesIds) {
        return newsRepo.findArticlesById(articlesIds);
    }

    @NotNull
    public Article parseArticle(@NotNull Post post, @NotNull User author) {
        Source source = new Source("", author.getName());
        String title = post.getTitle() != null ? post.getTitle() : "";
        String content = post.getContent() != null ? post.getContent() : "";
        String imageUri = post.getImageUri();
        String date = post.getDate() != null ? DateUtils.parseDateAndTime(post.getDate()) : null;

        Article art = new Article(
                0,
                source,
                author.getName(),
                title,
                content.substring(0, Math.min(DESCRIPTION_LENGTH, content.length())) + "...",
                author.getUsername(),
                imageUri,
                date
        );
        return art;
    }
}
