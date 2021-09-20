package com.revature.jash.datasource.documents;

import com.revature.jash.web.dtos.Principal;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Holds sets of questions
 * Names of Questions and Author
 *
 * */
@Data
@NoArgsConstructor
@Document(collection = "collections")
public class Collection {

    private String id;

    @NotBlank(message = "Title cannot be null or blank")
    private String title;

    @NotBlank(message = "Category cannot be null or blank")
    private String category;

    @NotNull(message = "Author cannot be null or blank")
    private Principal author;

    @NotBlank(message = "Description cannot be null or blank")
    private String description;

    private List<Question> questionList = new ArrayList<>();

    public Collection(String id, String title, String category, Principal author, String description, List<Question> questionList) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.author = author;
        this.description = description;
        this.questionList = questionList;
    }

    public Collection(Collection toCopy) {
        this.id = toCopy.id;
        this.title = toCopy.title;
        this.category = toCopy.category;
        this.author = toCopy.author;
        this.description = toCopy.description;
        this.questionList = cloneQuestions(toCopy.questionList);
    }

    public List<Question> cloneQuestions(List<Question> toCopy) {
        List<Question> result = new ArrayList<Question>();

        for(Question q : toCopy) {
            result.add(new Question(q));
        }

        return result;
    }
}
