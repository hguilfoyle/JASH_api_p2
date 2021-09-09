package com.revature.jash.services;

import com.revature.jash.datasource.documents.Collection;
import com.revature.jash.datasource.documents.User;
import com.revature.jash.datasource.repositories.CollectionRepository;
import com.revature.jash.datasource.repositories.UserRepository;
import com.revature.jash.util.PasswordUtils;
import com.revature.jash.util.exceptions.AuthenticationException;
import com.revature.jash.util.exceptions.InvalidRequestException;
import com.revature.jash.util.exceptions.ResourceNotFoundException;
import com.revature.jash.util.exceptions.ResourcePersistenceException;
import com.revature.jash.web.dtos.UserDTO;
import com.revature.jash.web.dtos.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final PasswordUtils passwordUtils;
    private final CollectionRepository collectionRepo;

    @Autowired
    public UserService(UserRepository userRepo, CollectionRepository collectionRepo, PasswordUtils passwordUtils) {
        this.userRepo = userRepo;
        this.collectionRepo = collectionRepo;
        this.passwordUtils = passwordUtils;
    }

    public List<UserDTO> findAll() {
        return userRepo.findAll()
                .stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    public UserDTO findById(String id) {

        if (id == null || id.trim().isEmpty()) {
            throw new InvalidRequestException("Invalid id provided");
        }

        return userRepo.findById(id)
                .map(UserDTO::new)
                .orElseThrow(ResourceNotFoundException::new);
    }

    public User register(User newUser) {

        if (!isUserValid(newUser)) {
            throw new InvalidRequestException("Invalid user data provided!");
        }

        if (userRepo.findUserByUsername(newUser.getUsername()) != null) {
            throw new ResourcePersistenceException("Provided username is already taken!");
        }

        if (userRepo.findUserByEmail(newUser.getEmail()) != null) {
            throw new ResourcePersistenceException("Provided email is already taken!");
        }

        String encryptedPassword = passwordUtils.generateSecurePassword(newUser.getPassword());
        newUser.setPassword(encryptedPassword);

        return userRepo.save(newUser);

    }

    public Principal login(String username, String password) {

        if (username == null || username.trim().equals("") || password == null || password.trim().equals("")) {
            throw new InvalidRequestException("Invalid user credentials provided!");
        }

        String encryptedPassword = passwordUtils.generateSecurePassword(password);
        User authUser = userRepo.findUserByUsernameAndPassword(username, encryptedPassword);

        if (authUser == null) {
            throw new AuthenticationException("Invalid credentials provided!");
        }

        return new Principal(authUser);

    }

    //This makes me sad, but it should work.
    public void delete(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidRequestException("Invalid id provided");
        }

        User user = userRepo.findById(id).orElseThrow(ResourceNotFoundException::new);

        //Maintain referential integrity
        for(Collection c : user.getCollections()) {
            //Need to remove collection from all Users favorites
            List<User> withFavorite = userRepo.findByFavoritesContaining(c); //Need to test this line!!!
            for(User u : withFavorite) {
                List<Collection> favorites = u.getFavorites();
                favorites.remove(c);
                u.setFavorites(favorites);
                userRepo.save(u);
            }

            //Finally, delete the collection.
            collectionRepo.deleteById(id);
        }

        userRepo.deleteById(id);
    }

    public User update(User user) {
        User toSave = userRepo.findById(user.getId()).orElseThrow(ResourceNotFoundException::new);

        /*
            Update each field, CANNOT UPDATE USERNAME OR PASSWORD OR COLLECTIONS
            Username and Password are login credentials that we should only update through a more secure
            and specific endpoint.
            Collections should be Created and Deleted through the collections endpoint, not through a User Update
         */

        toSave.setFirstName(user.getFirstName());
        toSave.setLastName(user.getLastName());
        toSave.setEmail(user.getEmail());

        return userRepo.save(user);
    }

    public void addCollection(Collection newCollection) {
        Principal authorPrincipal = newCollection.getAuthor();
        User author = userRepo.findById(authorPrincipal.getId()).orElseThrow(ResourceNotFoundException::new);;

        for(Collection c : author.getCollections()) {
            if(c.getId().equals(newCollection.getId())) {
                return;
            }
        }

        author.getCollections().add(newCollection);
        userRepo.save(author);
    }

    public void addFavorite(String user_id, String collection_id) {
        User user = userRepo.findById(user_id).orElseThrow(ResourceNotFoundException::new);
        Collection collection = collectionRepo.findById(collection_id).orElseThrow(ResourceNotFoundException::new);

        List<Collection> favorites = user.getFavorites();
        if(!favorites.contains(collection)) {
            favorites.add(collection);
        }
        user.setFavorites(favorites);
        userRepo.save(user);
    }

    public void removeFavorite(String user_id, String collection_id) {
        User user = userRepo.findById(user_id).orElseThrow(ResourceNotFoundException::new);
        Collection collection = collectionRepo.findById(collection_id).orElseThrow(ResourceNotFoundException::new);

        List<Collection> favorites = user.getFavorites();
        if(favorites.contains(collection)) {
            favorites.remove(collection);
        }
        user.setFavorites(favorites);
        userRepo.save(user);
    }

    public boolean isUsernameAvailable(String username) {

        if (username == null || username.trim().equals("")) {
            throw new InvalidRequestException("Invalid email value provided!");
        }

        return (userRepo.findUserByUsername(username) == null);
    }

    public boolean isEmailAvailable(String email) {

        if (email == null || email.trim().equals("")) {
            throw new InvalidRequestException("Invalid email value provided!");
        }

        return (userRepo.findUserByEmail(email) == null);
    }

    //TODO: Implement email verification
    public boolean isUserValid(User user) {
        if (user == null) return false;
        if (user.getFirstName() == null || user.getFirstName().trim().equals("")) return false;
        if (user.getLastName() == null || user.getLastName().trim().equals("")) return false;
        if (user.getEmail() == null || user.getEmail().trim().equals("")) return false;
        if (user.getUsername() == null || user.getUsername().trim().equals("")) return false;
        return user.getPassword() != null && !user.getPassword().trim().equals("");
    }

}

