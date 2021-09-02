package com.revature.jash.services;

import com.revature.jash.datasource.documents.User;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final PasswordUtils passwordUtils;

    @Autowired
    public UserService(UserRepository userRepo, PasswordUtils passwordUtils) {
        this.userRepo = userRepo;
        this.passwordUtils = passwordUtils;
    }

    public List<UserDTO> findAll() {
        return userRepo.findAll()
                .stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    public UserDTO findUserById(String id) {

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
            throw new ResourcePersistenceException("Provided username is already taken!");
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

    public void deleteById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidRequestException("Invalid id provided");
        }

        userRepo.deleteById(id);
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

