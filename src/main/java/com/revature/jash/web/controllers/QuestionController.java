package com.revature.jash.web.controllers;


import com.revature.jash.datasource.documents.Question;
import com.revature.jash.datasource.documents.User;
import com.revature.jash.services.CollectionService;
import com.revature.jash.services.QuestionService;
import com.revature.jash.util.exceptions.AuthenticationException;
import com.revature.jash.util.exceptions.UserForbiddenException;
import com.revature.jash.web.dtos.Principal;
import com.revature.jash.web.util.security.TokenParser;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * This Controller is specifically being used for the creation of custom questions by users.*/

@RestController
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;
    private final CollectionService collectionService;
    private final TokenParser parser;
    public QuestionController(QuestionService questionService, CollectionService collectionService, TokenParser parser){
            this.questionService = questionService;
            this.collectionService = collectionService;
            this.parser = parser;
        }

    @PostMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Question createQuestion(@RequestBody @Valid Question newQuestion, HttpServletRequest req){
        Principal principal = parser.parseToken(req).orElseThrow(() -> new AuthenticationException("Request originates from an unauthenticated source."));
        String requester = principal.getId();
        String accessed = collectionService.findCollectionById(newQuestion.getCollection_id()).getAuthor().getId();
        if(!requester.equals(accessed)) {
            throw new UserForbiddenException("Not allowed to create Questions in other Users' Collections");
        }


        return questionService.create(newQuestion);
    }

    @GetMapping(value = "{id}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public Question findById(@PathVariable String id) {
        return questionService.findById(id);
    }

    @DeleteMapping(value = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteQuestion(@PathVariable String id, HttpServletRequest req) {
        Principal principal = parser.parseToken(req).orElseThrow(() -> new AuthenticationException("Request originates from an unauthenticated source."));
        String requester = principal.getId();
        Question toDelete = questionService.findById(id);
        String accessed = collectionService.findCollectionById(toDelete.getCollection_id()).getAuthor().getId();
        if(!requester.equals(accessed)) {
            throw new UserForbiddenException("Not allowed to delete Questions that you dont own");
        }

        questionService.delete(id);
    }

    @PutMapping(value = "{id}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public Question updateQuestion(@RequestBody @Valid Question updatedQuestion, HttpServletRequest req) {
        Principal principal = parser.parseToken(req).orElseThrow(() -> new AuthenticationException("Request originates from an unauthenticated source."));
        String requester = principal.getId();
        String accessed = collectionService.findCollectionById(updatedQuestion.getCollection_id()).getAuthor().getId();
        if(!requester.equals(accessed)) {
            throw new UserForbiddenException("Not allowed to update Questions that you dont own");
        }

        return questionService.update(updatedQuestion);
    }


}
