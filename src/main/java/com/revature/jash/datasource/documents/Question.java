package com.revature.jash.datasource.documents;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "questions")
public class Question {

    private String id;

    @NotBlank(message = "Question cannot be null or blank")
    private String Question;

    @NotBlank(message = "Answer cannot be null or blank")
    private String Answer; // TODO : may want to remove this , but for now i will leave it in

    @NotBlank(message = "Category cannot be null or blank")
    private String Category; // the type of question (comedy , movie , art etc..)

    @NotBlank(message = "difficulty cannot be null or blank")
    private int difficultyLevel;// (between 0 - 4) this property describes how hard the question is to the average person

    @NotNull(message = "Author cannot be null or blank")
    private User author;

    private float pointValue; // (between 100 - 9999) this property denotes the amount of points this question is worth , if answered correctly
    private float penaltyValue; // this property denotes the amount of points this question deducts , if answered incorrectly
    private float multiplier; // (between 0.01 - 3.5) this property is a factor that we can use



    private List<String> hints; // TODO : may want to remove this , but for now i will leave it in
}

