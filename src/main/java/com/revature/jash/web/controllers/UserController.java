package com.revature.jash.web.controllers;

import com.revature.jash.datasource.documents.User;
import com.revature.jash.services.UserService;
import com.revature.jash.util.exceptions.AuthenticationException;
import com.revature.jash.util.exceptions.UserForbiddenException;
import com.revature.jash.web.dtos.UserDTO;
import com.revature.jash.web.dtos.Principal;
import com.revature.jash.web.filters.CorsFilter;
import com.revature.jash.web.util.security.Secured;
import com.revature.jash.web.util.security.SecurityAspect;
import com.revature.jash.web.util.security.TokenParser;
import org.springframework.boot.actuate.endpoint.annotation.FilteredEndpoint;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final TokenParser parser;

    public UserController(UserService userService, TokenParser parser) {
        this.userService = userService;
        this.parser = parser;
    }

    @GetMapping(produces = "application/json")
    @Secured(allowedUsers = {})
    public List<UserDTO> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping(value = "{id}", produces = "application/json")
    @Secured(allowedUsers = {})
    public UserDTO getUserById(@PathVariable String id) {
        return userService.findById(id);
    }

    @PostMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Principal registerNewUser(@RequestBody @Valid User newUser, HttpServletResponse resp) {
        resp.setHeader("Access-Control-Expose-Headers", "Authorization");
        return new Principal(userService.register(newUser));
    }

    @DeleteMapping(value = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable String id, HttpServletRequest req) {
        Principal principal = parser.parseToken(req).orElseThrow(() -> new AuthenticationException("Request originates from an unauthenticated source."));
        String requester = principal.getId();
        if(!requester.equals(id)) {
            throw new UserForbiddenException("Not allowed to delete other Users");
        }

        userService.delete(id);
    }
    
    @PutMapping(value = "{id}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@RequestBody @Valid User updatedUser, HttpServletRequest req) {
        Principal principal = parser.parseToken(req).orElseThrow(() -> new AuthenticationException("Request originates from an unauthenticated source."));
        String requester = principal.getId();
        String accessed = updatedUser.getId();
        if(!requester.equals(accessed)) {
            throw new UserForbiddenException("Not allowed to update other Users");
        }

        return userService.update(updatedUser);
    }

}
