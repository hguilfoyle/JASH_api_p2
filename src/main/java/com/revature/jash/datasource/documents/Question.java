package com.revature.jash.datasource.documents;
import java.util.List;

public class Question {
    private String Question;
    private String Answer; // TODO : may want to remove this , but for now i will leave it in
    private String Category; // the type of question (comedy , movie , art etc..)
    private int difficultyLevel;// (between 0 - 4) this property describes how hard the question is to the average person
    private float pointValue; // (between 100 - 9999) this property denotes the amount of points this question is worth , if answered correctly
    private float penaltyValue; // this property denotes the amount of points this question deducts , if answered incorrectly
    private float multiplier; // (between 0.01 - 3.5) this property is a factor that we can use
    private List<String> hints; // TODO : may want to remove this , but for now i will leave it in

    //for jakson
    public Question(){}

    public Question(String question, String answer, String category, int difficultyLevel) {
        Question = question;
        Answer = answer;
        Category = category;
        this.difficultyLevel = difficultyLevel;
    }
    public Question(String question, String answer, String category, int difficultyLevel , float pointValue) {
        new Question(question , answer , category , difficultyLevel);
        this.pointValue = pointValue;
    }

    public Question(String question, String answer, String category, int difficultyLevel , float pointValue , float penaltyValue) {
        new Question(question , answer , category , difficultyLevel , pointValue);
        this.penaltyValue = penaltyValue;
    }

    public Question(String question, String answer, String category, int difficultyLevel, float pointValue, float penaltyValue, float multiplier) {
        new Question(question , answer , category , difficultyLevel , pointValue , penaltyValue);
        this.multiplier = multiplier;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public List<String> getHints() {
        return hints;
    }

    public void setHints(List<String> hints) {
        this.hints = hints;
    }
}
