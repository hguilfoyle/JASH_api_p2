package com.revature.jash.services;

import com.revature.jash.datasource.documents.Collection;
import com.revature.jash.datasource.documents.Question;
import com.revature.jash.datasource.documents.User;
import com.revature.jash.datasource.repositories.CollectionRepository;
import com.revature.jash.datasource.repositories.QuestionRepository;
import com.revature.jash.util.exceptions.DuplicateResourceException;
import com.revature.jash.util.exceptions.InvalidRequestException;
import com.revature.jash.util.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectionService {
    private final CollectionRepository collectionRepo;
    private final UserService userService;

    @Autowired
    public CollectionService(CollectionRepository collectionRepository, UserService userService){
        this.collectionRepo = collectionRepository;
        this.userService = userService;
    }

    //Hoping that save updates instead of making a whole new thing
    public void addQuestionToCollection(Question question) {
        Collection collection = collectionRepo.findCollectionById(question.getCollection_id());
        collection.getQuestionList().add(question);
        collectionRepo.save(collection);
    }

    public Collection createNewCollection(Collection newCollection) {
        if(!isValid(newCollection)) {
            throw new InvalidRequestException("Invalid Collection");
        }

        if(!isUnique(newCollection)) {
            throw new DuplicateResourceException("Collection already exists", new Throwable());
        }

        newCollection = collectionRepo.save(newCollection);



        userService.addCollection(newCollection);
        return newCollection;
    }

    /**
     *  Takes in a collection with updated information ie. Title, Questions, Description and replaces the existing
     *  collection in the database with the newly updated Collection.
     *
     * @param updatedCollection
     * @return
     */
    public Collection replaceCollection(Collection updatedCollection) {
        if (!isValid(updatedCollection)) {
            throw new InvalidRequestException("Invalid Collection");
        }

        updatedCollection = collectionRepo.save(updatedCollection);

        return updatedCollection;
    }

    public void deleteCollectionById(String id) {
        collectionRepo.deleteById(id);
    }

    public boolean isUnique(Collection collection) {
        List<Collection> bySameAuthor = collectionRepo.findCollectionByAuthor(collection.getAuthor());
        for(Collection c : bySameAuthor) {
            if(c.getTitle().equals(collection.getTitle())) {
                return false;
            }
        }
        return true;
    }

    private boolean isValid(Collection collection) {
        return true;
    }

    public Collection findCollectionById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidRequestException("Invalid id provided");
        }

        return collectionRepo.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
    }
}
