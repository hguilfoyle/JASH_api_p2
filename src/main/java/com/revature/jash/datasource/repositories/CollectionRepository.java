package com.revature.jash.datasource.repositories;

import com.revature.jash.datasource.documents.Collection;
import com.revature.jash.datasource.documents.User;
import com.revature.jash.web.dtos.Principal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CollectionRepository extends MongoRepository<Collection, String> {

    //    @Query("{username: ?}") // You can optionally provide your own custom queries using this annotation
    Collection findCollectionById(String id);
    List<Collection> findCollectionByAuthor(Principal author);
    Collection findCollectionByCategory(String category);
}
