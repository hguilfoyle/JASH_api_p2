package com.revature.jash.services;

import com.revature.jash.datasource.documents.Collection;
import com.revature.jash.datasource.documents.Question;
import com.revature.jash.datasource.documents.User;
import com.revature.jash.datasource.repositories.CollectionRepository;
import com.revature.jash.datasource.repositories.QuestionRepository;
import com.revature.jash.datasource.repositories.UserRepository;
import com.revature.jash.util.exceptions.DuplicateResourceException;
import com.revature.jash.util.exceptions.InvalidRequestException;
import com.revature.jash.util.exceptions.ResourceNotFoundException;
import com.revature.jash.web.dtos.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollectionService {
    private final CollectionRepository collectionRepo;
    private final UserRepository userRepo;
    private final UserService userService;

    @Autowired
    public CollectionService(CollectionRepository collectionRepository, UserService userService, UserRepository userRepo){
        this.collectionRepo = collectionRepository;
        this.userService = userService;
        this.userRepo = userRepo;
    }

    public List<Collection> findAll() {
        return collectionRepo.findAll()
                .stream()
                .collect(Collectors.toList());
    }

    public Collection create(Collection newCollection) {
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


    public void delete(String id) {
        Collection toDelete = collectionRepo.findCollectionById(id);

        //Need to remove collection from the Author's collections
        User author = userRepo.findById(toDelete.getAuthor().getId()).orElseThrow(ResourceNotFoundException::new);
        List<Collection> collections = author.getCollections();
        collections.remove(toDelete);
        author.setCollections(collections);
        userRepo.save(author);

        //Need to remove collection from all Users favorites
        List<User> withFavorite = userRepo.findByFavoritesContaining(toDelete); //Need to test this line!!!
        for(User u : withFavorite) {
            List<Collection> favorites = author.getFavorites();
            favorites.remove(toDelete);
            author.setFavorites(favorites);
            userRepo.save(author);
        }

        //Finally, delete the collection.
        collectionRepo.deleteById(id);
    }

    /**
     *  Takes in a collection with updated information ie. Title, Questions, Description and replaces the existing
     *  collection in the database with the newly updated Collection.
     *
     * @param updatedCollection
     * @return
     */
    public Collection update(Collection updatedCollection) {
        if (!isValid(updatedCollection)) {
            throw new InvalidRequestException("Invalid Collection");
        }

        /*
            Update each field, CANNOT UPDATE QUESTION LIST OR AUTHOR
            Wont allow you to update the Author, that should be self-explanatory
            Questions should be Created and Deleted through the questions endpoint, not through a Collection Update
         */

        Collection oldCollection = collectionRepo.findCollectionById(updatedCollection.getId());
        Collection temp = collectionRepo.findCollectionById(updatedCollection.getId());

        temp.setCategory(updatedCollection.getCategory());
        temp.setDescription(updatedCollection.getDescription());
        temp.setTitle(updatedCollection.getTitle());

        updatedCollection = collectionRepo.save(temp);

        updateRI(updatedCollection);

        return updatedCollection;
    }

    //Manages Referential Integrity for Collection Updates
    public void updateRI(Collection updatedCollection) {
        Collection oldCollection = collectionRepo.findCollectionById(updatedCollection.getId());

        //Need to update collection in the Author's collections
        User author = userRepo.findById(updatedCollection.getAuthor().getId()).orElseThrow(ResourceNotFoundException::new);
        List<Collection> collections = author.getCollections();
        for(Collection c : collections) {
            if(c.getId().equals(updatedCollection.getId())) {
                collections.remove(c);
                collections.add(updatedCollection);
                break;
            }
        }
        author.setCollections(collections);
        userRepo.save(author);

        //Need to remove collection from all Users favorites
        List<User> withFavorite = userRepo.findByFavoritesContaining(oldCollection); //Need to test this line!!!
        for(User u : withFavorite) {
            List<Collection> favorites = author.getFavorites();
            for(Collection c : favorites) {
                if(c.getId().equals(updatedCollection.getId())) {
                    favorites.remove(c);
                    favorites.add(updatedCollection);
                    break;
                }
            }
            author.setFavorites(favorites);
            userRepo.save(author);
        }
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
