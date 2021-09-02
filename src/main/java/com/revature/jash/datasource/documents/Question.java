package com.revature.jash.datasource.documents;



import java.util.List;

public class Question {
    private String Question;
    private String Answer;
    private String Category;
    private int difficultyLevel;
    private float multiplier;
    private List<String> hints; // TODO : may want to remove this , but for now i will leave it in

    public Question(String question, String answer, String category, int difficultyLevel) {
        Question = question;
        Answer = answer;
        Category = category;
        this.difficultyLevel = difficultyLevel;
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
