package com.revature.jash.datasource.documents;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@Document(collection = "users")
public class User {

    private String id;

    @NotBlank(message = "Firstname cannot be null or blank")
    private String firstName;

    @NotBlank(message = "LastName cannot be null or blank")
    private String lastName;

    @Email
    @NotBlank(message = "Email cannot be null or blank")
    private String email;

    @Length(min = 5, max = 15)
    @NotBlank(message = "Username cannot be null or blank")
    private String username;

    @NotBlank(message = "Password cannot be null or blank")
    private String password;

    private List<Collection> collections = new ArrayList<>();
    private List<Collection> favorites = new ArrayList<>();

    public User(String id, String firstName, String lastName, String email, String username, String password){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public User(String firstName, String lastName, String email, String username, String password){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
    }
}
