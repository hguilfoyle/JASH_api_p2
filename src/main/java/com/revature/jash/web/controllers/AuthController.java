package com.revature.jash.web.controllers;

import com.revature.jash.services.UserService;
import com.revature.jash.web.dtos.Credentials;
import com.revature.jash.web.dtos.Principal;
import com.revature.jash.web.util.security.Secured;
import com.revature.jash.web.util.security.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final TokenGenerator tokenGenerator;

    @Autowired
    public AuthController(UserService userService, TokenGenerator tokenGenerator) {
        this.userService = userService;
        this.tokenGenerator = tokenGenerator;
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    public Principal authenticate(@RequestBody @Valid Credentials credentials, HttpServletResponse resp) {
        Principal principal = userService.login(credentials.getUsername(), credentials.getPassword());
        resp.setHeader(tokenGenerator.getJwtHeader(), tokenGenerator.createToken(principal));
        return principal;
    }

}

