package com.revature.jash.datasource.documents;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Holds sets of questions
 * Names of Questions and Author
 *
 * */
@Data
@NoArgsConstructor
@Document(collection = "collection-data")
public class Collection {

    private String id;
}
