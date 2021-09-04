package com.revature.jash.web.controllers;


import com.revature.jash.datasource.documents.Question;
import com.revature.jash.datasource.documents.User;
import com.revature.jash.services.QuestionService;
import com.revature.jash.web.dtos.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * This Controller is specifically being used for the creation of custom questions by users.*/

@RestController
@RequestMapping("/questions")
@CrossOrigin
public class QuestionController {

        private final QuestionService questionService;

        public QuestionController(QuestionService questionService){
            this.questionService = questionService;
        }

    @PostMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Question createQuestion(@RequestBody @Valid Question newQuestion){
            return questionService.createNewQuestion(newQuestion);
    }

}
