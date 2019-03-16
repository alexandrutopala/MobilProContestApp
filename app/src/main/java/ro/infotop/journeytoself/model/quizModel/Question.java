package ro.infotop.journeytoself.model.quizModel;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Objects;

import ro.infotop.journeytoself.commons.Disease;

public class Question implements Serializable {
    private long id;
    private long matcherCode;
    private String question;
    private LinkedHashMap<String, Integer> answers;
    private Disease category;
    private String chosenAnswer;

    public Question(String question, LinkedHashMap<String, Integer> answers, Disease category) {
        this.question = question;
        this.answers = answers;
        this.category = category;
        this.matcherCode = Disease.findMask(category);
    }

    public Question(long id, String question, LinkedHashMap<String, Integer> answers, Disease category) {
        this(question, answers, category);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMatcherCode() {
        return matcherCode;
    }

    public void setMatcherCode(long matcherCode) {
        this.matcherCode = matcherCode;
    }

    public Disease getCategory() {
        return category;
    }

    public void setCategory(Disease category) {
        this.category = category;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public LinkedHashMap<String, Integer> getAnswers() {
        return answers;
    }

    public void setAnswers(LinkedHashMap<String, Integer> answers) {
        this.answers = answers;
    }

    public String getChosenAnswer() {
        return chosenAnswer;
    }

    public void setChosenAnswer(String chosenAnswer) {
        this.chosenAnswer = chosenAnswer;
    }

    public int getMaximumScore() {
        return Collections.max(answers.values());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Question)) return false;
        Question question = (Question) o;
        return id == question.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
