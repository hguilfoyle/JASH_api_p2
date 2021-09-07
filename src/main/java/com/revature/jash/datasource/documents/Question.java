package com.revature.jash.datasource.documents;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "questions")
public class Question {

    private String id;

    @NotBlank
    private String collection_id;

    @NotBlank(message = "Question cannot be null or blank")
    private String question;

    @NotBlank(message = "Answer cannot be null or blank")
    private String answer; // TODO : may want to remove this , but for now i will leave it in

    @NotBlank(message = "Category cannot be null or blank")
    private String category; // the type of question (comedy , movie , art etc..)

    @NotNull(message = "difficulty cannot be null")
    @Min(0)
    @Max(4)
    private int value;// (between 0 - 4) this property describes how hard the question is to the average person

    private float penaltyValue; // this property denotes the amount of points this question deducts , if answered incorrectly
    private float multiplier; // (between 0.01 - 3.5) this property is a factor that we can use

    private List<String> hints = new ArrayList<>(); // TODO : may want to remove this , but for now i will leave it in

    public Question(String id, String collection_id, String question, String answer, String category, int value, float penaltyValue, float multiplier, List<String> hints) {
        this.id = id;
        this.collection_id = collection_id;
        this.question = question;
        this.answer = answer;
        this.category = category;
        this.value = value;
        this.penaltyValue = penaltyValue;
        this.multiplier = multiplier;
        this.hints = hints;
    }

}

