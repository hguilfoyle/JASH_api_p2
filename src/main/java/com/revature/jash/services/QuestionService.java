package com.revature.jash.services;

import com.revature.jash.datasource.documents.Question;
import com.revature.jash.datasource.repositories.QuestionRepository;
import com.revature.jash.util.exceptions.InvalidRequestException;
import com.revature.jash.util.exceptions.ResourceNotFoundException;
import com.revature.jash.web.dtos.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {

    private final QuestionRepository questionRepo;
    private final CollectionService collectionService;

    @Autowired
    public QuestionService(QuestionRepository questionRepo, CollectionService collectionService){
        this.questionRepo = questionRepo;
        this.collectionService = collectionService;
    }

    public Question createNewQuestion(Question newQuestion) {
        if(!isValid(newQuestion)) {
            throw new InvalidRequestException("Invalid Question");
        }

        collectionService.addQuestionToCollection(newQuestion);
        return questionRepo.save(newQuestion);
    }

    public Question findQuestionById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidRequestException("Invalid id provided");
        }

        return questionRepo.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
    }

    //TODO: Implement me
    private boolean isValid(Question question) {
        return true;
    }
}
