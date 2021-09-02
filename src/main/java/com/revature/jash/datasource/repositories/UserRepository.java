package com.revature.jash.datasource.repositories;

import com.revature.jash.datasource.documents.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends MongoRepository<User, String> {

    //    @Query("{username: ?}") // You can optionally provide your own custom queries using this annotation
    User findUserByUsernameAndPassword(String username, String password);
    User findUserByUsername(String username);
    User findUserByEmail(String email);

}

