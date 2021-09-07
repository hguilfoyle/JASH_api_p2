package com.revature.jash.datasource.repositories;

import com.revature.jash.datasource.documents.Collection;
import com.revature.jash.datasource.documents.Question;
import com.revature.jash.datasource.documents.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository extends MongoRepository<User, String> {

    //    @Query("{username: ?}") // You can optionally provide your own custom queries using this annotation
    User findUserByUsernameAndPassword(String username, String password);
    User findUserByUsername(String username);
    User findUserByEmail(String email);

    List<User> findByFavoritesContaining(Collection toDelete);

    //Dont know if this works
    //Want to find Users->Favorites->QuestionList containing toDelete
    List<User> findByFavoritesContaining(Question toDelete);

    //List<User> findByFavoritesContainingQuestionListContaining(Question toDelete) maybee???
}

