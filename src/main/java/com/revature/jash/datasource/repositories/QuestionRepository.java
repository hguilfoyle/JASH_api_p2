package com.revature.jash.datasource.repositories;

import com.revature.jash.datasource.documents.Question;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface QuestionRepository extends MongoRepository<Question, String> {

    //    @Query("{username: ?}") // You can optionally provide your own custom queries using this annotation
    Question findQuestionById(String id);

}

