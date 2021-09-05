package com.revature.jash.datasource.documents;

import com.revature.jash.web.dtos.Principal;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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

}
