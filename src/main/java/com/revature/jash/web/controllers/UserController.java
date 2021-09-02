package com.revature.jash.web.controllers;

import com.revature.jash.datasource.documents.User;
import com.revature.jash.services.UserService;
import com.revature.jash.web.dtos.UserDTO;
import com.revature.jash.web.dtos.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "test", produces = "application/json")
    public String test() {
        return "HIT IT";
    }

    @GetMapping(produces = "application/json")
    public List<UserDTO> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping(value = "{id}", produces = "application/json")
    public UserDTO getUserById(@PathVariable String id) {
        return userService.findUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Principal registerNewUser(@RequestBody User newUser) {
        return new Principal(userService.register(newUser));
    }

}