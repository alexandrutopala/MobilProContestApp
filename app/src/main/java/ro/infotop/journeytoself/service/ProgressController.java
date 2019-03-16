package ro.infotop.journeytoself.service;

import java.util.Map;

import ro.infotop.journeytoself.model.userModel.User;
import ro.infotop.journeytoself.repository.ProgressRepo;

public class ProgressController {
    private static final ProgressController ourInstance = new ProgressController();

    public static ProgressController getInstance() {
        return ourInstance;
    }

    private ProgressRepo progressRepo;

    private ProgressController() {
        progressRepo = new ProgressRepo(true);
    }


    public void persistTodayQuiz(Map<String, String> qa, Double result, User user) {
        progressRepo.persistTodayQuiz(qa, result, user);
    }

    public boolean isTodayQuizCompleted(User user) {
        return progressRepo.isQuizCompletedForToday(user);
    }

    public boolean hadPostedInDiaryToday(User user) {
        return progressRepo.hadPostedInDiaryToday(user);
    }

    public Double getTodayQuizResult(User user) {
        return progressRepo.getTodayQuizResult(user);
    }

    public void markTodayUserPostedInDiary(User user) {
        progressRepo.markTodayUserPostedInDiary(user);
    }

    public Map<String, Double> getAllScores(User user) {
        return progressRepo.getAllScores(user);
    }
}
