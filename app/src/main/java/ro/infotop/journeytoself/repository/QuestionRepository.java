package ro.infotop.journeytoself.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ro.infotop.journeytoself.commons.Disease;
import ro.infotop.journeytoself.model.quizModel.Question;
import ro.infotop.journeytoself.service.IllnessesController;

public class QuestionRepository extends DelayedRepository {

    private static List<Question> questions;
    private static long idGenerator = 0;

    static {
        initQuestions();
    }

    public QuestionRepository(boolean simulateDelay) {
        super(simulateDelay);
    }

    public List<Question> findAllQuestionsByCategory(Disease category) {
        if (simulateDelay) sleep(DEFAULT_LATENCY);

        List<Question> list = new ArrayList<>();
        for (Question q : questions) {
            if (q.getCategory().equals(category)) {
                list.add(q);
            }
        }
        return list;
    }


    private static void initQuestions() {
        questions = new ArrayList<>();
        LinkedHashMap<String, Integer> answers = new LinkedHashMap<>();

        ///////
        String q = "Cat de bine ai mancat?";
        answers.put("Foarte bine", 4);
        answers.put("Bine", 3);
        answers.put("Nu prea mult", 2);
        answers.put("Deloc", 0);

        questions.add(new Question(
                idGenerator++, q, answers, Disease.STRESS
        ));

        answers = new LinkedHashMap<>();

        ///////
        q = "Cat de bine ai dormit?";
        answers.put("Foarte bine", 4);
        answers.put("Bine", 3);
        answers.put("Nu prea mult", 2);
        answers.put("Deloc", -1);

        questions.add(new Question(
                idGenerator++, q, answers, Disease.STRESS
        ));

        answers = new LinkedHashMap<>();
        ///////
        q = "Ai socializat azi?";
        answers.put("Da, foarte mult", 4);
        answers.put("Da, putin", 3);
        answers.put("Nu prea mult", 2);
        answers.put("Deloc", -1);

        questions.add(new Question(
                idGenerator++, q, answers, Disease.ANXIETY
        ));

        answers = new LinkedHashMap<>();
        ///////
        q = "Cat de bine te simti?";
        answers.put("Foarte bine", 4);
        answers.put("Bine", 3);
        answers.put("Am avut si zile mai bune", 2);
        answers.put("Rau", -1);

        questions.add(new Question(
                idGenerator++, q, answers, Disease.DEPRESSION
        ));

        answers = new LinkedHashMap<>();
        ///////
        q = "Ai invatat pentru examen?";
        answers.put("Da, din timpul semestrului", 4);
        answers.put("Da, in sesiune", 3);
        answers.put("Da, in timpul examenului", 2);
        answers.put("Doamne ajuta", -1);

        questions.add(new Question(
                idGenerator++, q, answers, Disease.DEPRESSION
        ));

        answers = new LinkedHashMap<>();
        ///////
        q = "Cum ai reactionat cand ai vazut un paianjen ultima oara?";
        answers.put("L-am strivit", 4);
        answers.put("Am iesit din camera", 3);
        answers.put("Am tipat", 2);

        questions.add(new Question(
                idGenerator++, q, answers, Disease.PHOBIA
        ));

        answers = new LinkedHashMap<>();
        ///////
        q = "Cat de mult sport ai facut azi?";
        answers.put("Mult", 4);
        answers.put("Destul de mult", 3);
        answers.put("Putin", 2);
        answers.put("Deloc", -1);

        questions.add(new Question(
                idGenerator++, q, answers, Disease.STRESS
        ));

        answers = new LinkedHashMap<>();

        ///////
        q = "Ai comunicat cu prietenii tai azi?";
        answers.put("Da", 4);
        answers.put("Nu", -1);

        questions.add(new Question(
                idGenerator++, q, answers, Disease.DEPRESSION
        ));

        answers = new LinkedHashMap<>();
        ///////
        q = "Cat de bine ai comunicat cu partenerul tau azi?";
        answers.put("Foarte bine", 4);
        answers.put("Bine", 3);
        answers.put("Se putea mai bine", 2);
        answers.put("Nu ne-am inteles deloc", -1);

        questions.add(new Question(
                idGenerator++, q, answers, Disease.COUPLE_PROBLEMS
        ));

        answers = new LinkedHashMap<>();
        ///////
        q = "Cat de bine ai lucrat in echipa cu partenerul?";
        answers.put("Foarte bine", 4);
        answers.put("Bine", 3);
        answers.put("Nu prea bine", 2);
        answers.put("Total desincronizati", -1);

        questions.add(new Question(
                idGenerator++, q, answers, Disease.COUPLE_PROBLEMS
        ));

        answers = new LinkedHashMap<>();
        ///////
        q = "Cat de bine ai comunicat cu parintii azi?";
        answers.put("Foarte bine", 4);
        answers.put("Bine", 3);
        answers.put("Nu prea bine", 2);
        answers.put("Ne-am certat", -1);

        questions.add(new Question(
                idGenerator++, q, answers, Disease.FAMILY_PROBLEMS
        ));

        answers = new LinkedHashMap<>();
        ///////
        q = "Ai ascultat de parinti azi?";
        answers.put("Da", 4);
        answers.put("Nu", 2);
        answers.put("Dar ei asculta de mine?", -1);

        questions.add(new Question(
                idGenerator++, q, answers, Disease.FAMILY_PROBLEMS
        ));

        answers = new LinkedHashMap<>();
        ///////
        q = "Te simti incomod in spatii inchise?";
        answers.put("Da", -1);
        answers.put("Nu", 4);

        questions.add(new Question(
                idGenerator++, q, answers, Disease.PHOBIA
        ));

        answers = new LinkedHashMap<>();
        ///////
        q = "Le-ai spus azi parintilor cum a fost ziua ta?";
        answers.put("Da", 4);
        answers.put("Nu", -1);

        questions.add(new Question(
                idGenerator++, q, answers, Disease.FAMILY_PROBLEMS
        ));
    }

    public List<Question> findQuestionsByMatcherCode(long matcherCode, final int howMany) {
        if (simulateDelay) sleep(DEFAULT_LATENCY);

        List<Question> list = new ArrayList<>();

        for (Question q : questions) {
            if (IllnessesController.codesAreMatching(matcherCode, q.getMatcherCode())) {
                if (list.size() >= howMany) {
                    return list;
                }

                list.add(q);
            }
        }
        return list;
    }

    public void persistQuestion(Question q) {
        if (simulateDelay) sleep(SHORT_LATENCY);
        questions.remove(q);
        questions.add(q);
    }
}
