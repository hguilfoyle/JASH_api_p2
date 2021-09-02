package com.revature.jash.datasource.repositories;

import com.revature.jash.datasource.documents.Collection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CollectionRepository extends MongoRepository<Collection, String> {

    //    @Query("{username: ?}") // You can optionally provide your own custom queries using this annotation
    Collection findCollectionById(String id);

}
