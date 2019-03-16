package ro.infotop.journeytoself.model.progressModel;

import java.util.Map;

import ro.infotop.journeytoself.model.userModel.User;

public class ProgressBundle {
    private User user;
    private Map<String, String> qa;
    private Double quizScore;
    private boolean hadPostInDiaryToday;
    private String date;

    public ProgressBundle(User user, String date) {
        this.user = user;
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Map<String, String> getQa() {
        return qa;
    }

    public void setQa(Map<String, String> qa) {
        this.qa = qa;
    }

    public Double getQuizScore() {
        return quizScore;
    }

    public void setQuizScore(Double quizScore) {
        this.quizScore = quizScore;
    }

    public boolean isHadPostInDiaryToday() {
        return hadPostInDiaryToday;
    }

    public void setHadPostInDiaryToday(boolean hadPostInDiaryToday) {
        this.hadPostInDiaryToday = hadPostInDiaryToday;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double calculateScore() {
        double score = 0;
        if (quizScore != null) {
            score += 100 * quizScore;
        }

        if (hadPostInDiaryToday) {
            score += 200.0;
        }
        return score;
    }

}
