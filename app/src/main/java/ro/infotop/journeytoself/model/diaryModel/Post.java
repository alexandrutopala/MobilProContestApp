package ro.infotop.journeytoself.model.diaryModel;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import ro.infotop.journeytoself.model.userModel.User;

public class Post implements Serializable {
    private long id;
    private String title;
    private Date date;
    private String content;
    private User user;
    private String imageUri;

    public Post() {
    }

    public Post(long id, String title, Date date, String content) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.content = content;
    }

    public Post(long id, String title, Date date, String content, User user) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.content = content;
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String uri) {
        this.imageUri = uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;
        Post post = (Post) o;
        return id == post.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
