package com.revature.jash.services;

import com.revature.jash.datasource.documents.Question;
import com.revature.jash.datasource.repositories.QuestionRepository;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {

    private final QuestionRepository questionRepo;

    public QuestionService(QuestionRepository questionRepo){
        this.questionRepo = questionRepo;
    }

    public Question createNewQuestion(Question newQuestion) {

        return questionRepo.save(newQuestion);
    }
}
