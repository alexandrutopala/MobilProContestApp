package ro.infotop.journeytoself.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ro.infotop.journeytoself.model.diaryModel.Post;
import ro.infotop.journeytoself.model.userModel.User;
import ro.infotop.journeytoself.utils.DateUtils;

public class PostsRepo extends DelayedRepository {
    private final static int SHORT_LATENCY = 500;
    private static final int DEFAULT_LATENCY = 1000;
    private static final int LONG_LATENCY = 3000;
    private static long idGenerator = 0;

    private List<Post> mockPostsDb; // the integer is the corresponding user id
    private User user;

    public PostsRepo(User user) {
        super(false);
        this.user = user;
        initMockDb();
    }

    public PostsRepo(User user, boolean simulateDelay) {
        super(simulateDelay);
        this.user = user;
        initMockDb();
    }

    public int getPostsCount() {
        return mockPostsDb.size();
    }

    public List<Post> getPosts(Date lastUpdate, int fromIndex, int toIndex) {
        if (simulateDelay) sleep(SHORT_LATENCY);

        Iterator<Post> it = mockPostsDb.iterator();
        int index = 0;
        List<Post> posts = new ArrayList<>();

        while (it.hasNext() && index < toIndex) {
            if (index++ < fromIndex) {
                it.next();
                continue;
            }

            Post p = it.next();
            posts.add(p);
        }

        return posts;
    }

    public Post findPostById(long id) {
        if (simulateDelay) sleep(SHORT_LATENCY);

        for (Post post : mockPostsDb) {
            if (post.getId() == id) {
                return post;
            }
        }
        return null;
    }

    public List<Post> findPostsById(long[] postsIds) {
        List<Post> posts = new ArrayList<>();

        for (long id : postsIds) {
            for (Post p : mockPostsDb) {
                if (p.getId() == id) {
                    posts.add(p);
                    break;
                }
            }
        }

        return posts;
    }


    public boolean persistPost(Post post) {
        if (simulateDelay) sleep(LONG_LATENCY);

        mockPostsDb.remove(post);
        mockPostsDb.add(0, post);
        return true;
    }

    public boolean hadPosted(User who, Date when) {
        if (simulateDelay) sleep(DEFAULT_LATENCY);

        String simplifiedDate = DateUtils.parseDateAndTime(when);

        for (Post p : mockPostsDb) {
            if (p.getUser().equals(who)) {
                String sd = DateUtils.parseDateAndTime(p.getDate());
                if (simplifiedDate.equalsIgnoreCase(sd)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void initMockDb() {
        if (simulateDelay) sleep(LONG_LATENCY);

        // TODO: insert entries
        mockPostsDb = new ArrayList<>();
        Post p1 = new Post(idGenerator++, "Notita mea 1", DateUtils.parseStringDateAndTime("22.01.2019-23:15"), "Prima zi in pustietate", user);
        Post p2 = new Post(idGenerator++, "Notita mea 2", DateUtils.parseStringDateAndTime("25.01.2019-20:35"), "Am reusit sa gasesc apa potabila si hrana", user);
        Post p3 = new Post(idGenerator++, "Notita mea 3", DateUtils.parseStringDateAndTime("26.01.2019-13:05"), "Mama avea dreptate, trebuia sa ma spal pe maini inainte sa mananc", user);
        Post p4 = new Post(idGenerator++, "Notita mea 4", DateUtils.parseStringDateAndTime("29.01.2019-03:15"), "Am facut un anunt de SOS. Oare o sa-l vada cineva?", user);
        Post p5 = new Post(idGenerator++, "Notita mea 5", DateUtils.parseStringDateAndTime("02.02.2019-10:30"), "Prima zi de pescuit. Era sa ma manance un rechin", user);
        Post p6 = new Post(idGenerator++, "Notita mea 6", DateUtils.parseStringDateAndTime("23.02.2019-11:17"), "Cum de mai am baterie la telefon? Si cum de am semnal? De ce oare n-am sunat dupa ajutoare pana acum", user);
        Post p7 = new Post(idGenerator++, "Notita mea 7", DateUtils.parseStringDateAndTime("24.02.2019-22:19"), "Se pare ca de pe cealalta parte a insulei se vedea tarmul. S-ar zice ca sunt salvat", user);
        Post p8 = new Post(idGenerator++, "Notita mea 8", DateUtils.parseStringDateAndTime("24.02.2019-23:19"), "Se pare ca de pe cealalta parte a insulei se vedea tarmul. S-ar zice ca sunt salvat", user);
        Post p9 = new Post(idGenerator++, "Notita mea 9", DateUtils.parseStringDateAndTime("25.02.2019-01:09"), "Se pare ca de pe cealalta parte a insulei se vedea tarmul. S-ar zice ca sunt salvat", user);
        Post p10 = new Post(idGenerator++, "Notita mea 10", DateUtils.parseStringDateAndTime("25.02.2019-10:20"), "Se pare ca de pe cealalta parte a insulei se vedea tarmul. S-ar zice ca sunt salvat", user);
        Post p11 = new Post(idGenerator++, "Notita mea 11", DateUtils.parseStringDateAndTime("01.03.2019-12:19"), "Se pare ca de pe cealalta parte a insulei se vedea tarmul. S-ar zice ca sunt salvat", user);
        Post p12 = new Post(idGenerator++, "Notita mea 12", DateUtils.parseStringDateAndTime("02.03.2019-09:45"), "Se pare ca de pe cealalta parte a insulei se vedea tarmul. S-ar zice ca sunt salvat", user);

        mockPostsDb.add(p12);
        mockPostsDb.add(p11);
        mockPostsDb.add(p10);
        mockPostsDb.add(p9);
        mockPostsDb.add(p8);
        mockPostsDb.add(p7);
        mockPostsDb.add(p6);
        mockPostsDb.add(p5);
        mockPostsDb.add(p4);
        mockPostsDb.add(p3);
        mockPostsDb.add(p2);
        mockPostsDb.add(p1);

    }



}
