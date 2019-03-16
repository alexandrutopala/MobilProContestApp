package ro.infotop.journeytoself.service;

import java.util.List;

import ro.infotop.journeytoself.commons.Disease;
import ro.infotop.journeytoself.model.quizModel.Question;
import ro.infotop.journeytoself.repository.QuestionRepository;

public class QuestionsController {
    public static final int FEW_QUESTIONS = 5;
    public static final int MORE_QUESTIONS = 9;
    public static final int A_LOT_OF_QUESTIONS = 20;
    private static final QuestionsController ourInstance = new QuestionsController();
    private QuestionRepository questionRepo;

    public static QuestionsController getInstance() {
        return ourInstance;
    }

    private QuestionsController() {
        questionRepo = new QuestionRepository(true);
    }

    public List<Question> findAllQuestionsByCategory(Disease category) {
        return questionRepo.findAllQuestionsByCategory(category);
    }

    public List<Question> findQuestionsByMatcherCode(long matcherCode, int howMany) {
        return questionRepo.findQuestionsByMatcherCode(matcherCode, howMany);
    }

    public void persistQuestion(Question q) {
        questionRepo.persistQuestion(q);
    }
}
