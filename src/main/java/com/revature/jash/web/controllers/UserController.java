package com.revature.jash.web.controllers;

import com.revature.jash.datasource.documents.User;
import com.revature.jash.services.UserService;
import com.revature.jash.web.dtos.UserDTO;
import com.revature.jash.web.dtos.Principal;
import com.revature.jash.web.filters.CorsFilter;
import com.revature.jash.web.util.security.Secured;
import org.springframework.boot.actuate.endpoint.annotation.FilteredEndpoint;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(produces = "application/json")
    @Secured(allowedUsers = {})
    public List<UserDTO> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping(value = "{id}", produces = "application/json")
    @Secured(allowedUsers = {})
    public UserDTO getUserById(@PathVariable String id) {
        return userService.findUserById(id);
    }

    @PostMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Principal registerNewUser(@RequestBody @Valid User newUser, HttpServletResponse resp) {
        resp.setHeader("Access-Control-Expose-Headers", "Authorization");
        return new Principal(userService.register(newUser));
    }

    @DeleteMapping(value = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable String id) {
        userService.deleteById(id);
    }

    @PutMapping(value = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateUser(@RequestBody @Valid User updatedUser) {
        userService.update(updatedUser);
    }

}