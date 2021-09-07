package com.revature.jash.web.dtos;

import com.revature.jash.datasource.documents.Collection;
import com.revature.jash.datasource.documents.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserDTO {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;

    private List<Collection> collections = new ArrayList<>();
    private List<Collection> favorites = new ArrayList<>();

    public UserDTO(User subject) {
        this.id = subject.getId();
        this.firstName = subject.getFirstName();
        this.lastName = subject.getLastName();
        this.email = subject.getEmail();
        this.username = subject.getUsername();
        this.collections = subject.getCollections();
        this.favorites = subject.getFavorites();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Collection> getCollections() {
        return collections;
    }

    public void setCollections(List<Collection> collections) {
        this.collections = collections;
    }

    public List<Collection> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<Collection> favorites) {
        this.favorites = favorites;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(id, userDTO.id) && Objects.equals(firstName, userDTO.firstName) && Objects.equals(lastName, userDTO.lastName) && Objects.equals(email, userDTO.email) && Objects.equals(username, userDTO.username) && Objects.equals(collections, userDTO.collections) && Objects.equals(favorites, userDTO.favorites);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, username, collections, favorites);
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", collections=" + collections +
                ", favorites=" + favorites +
                '}';
    }
}
