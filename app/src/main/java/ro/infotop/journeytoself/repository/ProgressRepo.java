package ro.infotop.journeytoself.repository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import ro.infotop.journeytoself.model.progressModel.ProgressBundle;
import ro.infotop.journeytoself.model.userModel.User;
import ro.infotop.journeytoself.service.UserController;
import ro.infotop.journeytoself.utils.DateUtils;

public class ProgressRepo extends DelayedRepository {

    private static Map<User, Map<String, ProgressBundle>> progressDb;

    static {
        progressDb = new HashMap<>();
        initMockDb();
    }

    public ProgressRepo(boolean simulateDelay) {
        super(simulateDelay);
    }

    private void checkProgressBundleForDateAndUser(String date, User user) {
        if (!progressDb.containsKey(user)) {
            progressDb.put(user, new HashMap<>());
        }

        Map<String, ProgressBundle> userProgress = progressDb.get(user);

        if (!userProgress.containsKey(date)) {
            userProgress.put(date, new ProgressBundle(user, date));
        }
    }

    public void persistTodayQuiz(Map<String, String> qa, Double score, User user) {
        String currentDate = DateUtils.getCurrentDateAsString();
        checkProgressBundleForDateAndUser(currentDate, user);

        ProgressBundle pb = progressDb.get(user).get(currentDate);
        pb.setQa(qa);
        pb.setQuizScore(score);
    }

    public void markTodayUserPostedInDiary(User user) {
        String currentDate = DateUtils.getCurrentDateAsString();
        checkProgressBundleForDateAndUser(currentDate, user);

        ProgressBundle pb = progressDb.get(user).get(currentDate);
        pb.setHadPostInDiaryToday(true);
    }

    public boolean isQuizCompletedForToday(User user) {
        return getTodayQuizResult(user) != null;
    }

    public boolean hadPostedInDiaryToday(User user) {
        String currentDate = DateUtils.getCurrentDateAsString();
        checkProgressBundleForDateAndUser(currentDate, user);

        ProgressBundle pb = progressDb.get(user).get(currentDate);
        return pb.isHadPostInDiaryToday();
    }

    public Map<String, Double> getAllScores(User user) {
        if (simulateDelay) sleep(LONG_LATENCY);

        String currentDate = DateUtils.getCurrentDateAsString();
        checkProgressBundleForDateAndUser(currentDate, user);

        Map<String, ProgressBundle> progress = progressDb.get(user);

        Comparator<String> comp = (d1, d2) -> DateUtils.parseStringDate(d1).compareTo(DateUtils.parseStringDate(d2));
        Map<String, Double> scores = new TreeMap<>(comp);

        for (String date : progress.keySet()) {
            scores.put(
                    date,
                    progress.get(date).calculateScore()
            );
        }
        return scores;
    }

    public Double getTodayQuizResult(User user) {
        String currentDate = DateUtils.getCurrentDateAsString();
        checkProgressBundleForDateAndUser(currentDate, user);

        ProgressBundle pb = progressDb.get(user).get(currentDate);
        return pb.getQuizScore();
    }

    private static void initMockDb() {
        Comparator<String> comp = (d1, d2) -> - DateUtils.parseStringDate(d1).compareTo(DateUtils.parseStringDate(d2));

        User user;
        Map<String, ProgressBundle> progress = new TreeMap<>(comp);
        ProgressBundle pg;
        String date;

        user = UserController.getInstance().findUserById(0);

        date = "22.02.2019";
        pg = new ProgressBundle(user, date);
        pg.setQuizScore(6.5);
        pg.setHadPostInDiaryToday(true);

        progress.put(date, pg);

        date = "21.02.2019";
        pg = new ProgressBundle(user, date);
        pg.setQuizScore(5.6);
        pg.setHadPostInDiaryToday(false);

        progress.put(date, pg);

        date = "20.02.2019";
        pg = new ProgressBundle(user, date);
        pg.setQuizScore(4.5);
        pg.setHadPostInDiaryToday(true);

        progress.put(date, pg);

        date = "19.02.2019";
        pg = new ProgressBundle(user, date);
        pg.setQuizScore(6.3);
        pg.setHadPostInDiaryToday(false);

        progress.put(date, pg);

        date = "18.02.2019";
        pg = new ProgressBundle(user, date);
        pg.setQuizScore(6.5);
        pg.setHadPostInDiaryToday(false);

        progress.put(date, pg);

        date = "17.02.2019";
        pg = new ProgressBundle(user, date);
        pg.setQuizScore(7.5);
        pg.setHadPostInDiaryToday(true);

        progress.put(date, pg);

        date = "16.02.2019";
        pg = new ProgressBundle(user, date);
        pg.setQuizScore(7.0);
        pg.setHadPostInDiaryToday(true);

        progress.put(date, pg);

        date = "15.02.2019";
        pg = new ProgressBundle(user, date);
        pg.setQuizScore(8.0);
        pg.setHadPostInDiaryToday(true);

        progress.put(date, pg);

        date = "14.02.2019";
        pg = new ProgressBundle(user, date);
        pg.setQuizScore(7.3);
        pg.setHadPostInDiaryToday(true);

        progress.put(date, pg);

        date = "13.02.2019";
        pg = new ProgressBundle(user, date);
        pg.setQuizScore(7.4);
        pg.setHadPostInDiaryToday(false);

        progress.put(date, pg);

        date = "12.02.2019";
        pg = new ProgressBundle(user, date);
        pg.setQuizScore(8.5);
        pg.setHadPostInDiaryToday(true);

        progress.put(date, pg);

        progressDb.put(user, progress);
    }
}
