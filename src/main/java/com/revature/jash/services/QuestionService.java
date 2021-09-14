package com.revature.jash.services;

import com.revature.jash.datasource.documents.Collection;
import com.revature.jash.datasource.documents.Question;
import com.revature.jash.datasource.documents.User;
import com.revature.jash.datasource.repositories.CollectionRepository;
import com.revature.jash.datasource.repositories.QuestionRepository;
import com.revature.jash.datasource.repositories.UserRepository;
import com.revature.jash.util.exceptions.InvalidRequestException;
import com.revature.jash.util.exceptions.ResourceNotFoundException;
import com.revature.jash.web.dtos.Principal;
import com.revature.jash.web.dtos.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepo;
    private final CollectionService collectionService;
    private final CollectionRepository collectionRepo;
    private final UserRepository userRepo;

    @Autowired
    public QuestionService(QuestionRepository questionRepo, CollectionService collectionService, CollectionRepository collectionRepo, UserRepository userRepo){
        this.questionRepo = questionRepo;
        this.collectionService = collectionService;
        this.collectionRepo = collectionRepo;
        this.userRepo = userRepo;
    }

    public Question create(Question newQuestion) {

        newQuestion = questionRepo.save(newQuestion);

        Collection collection = collectionRepo.findCollectionById(newQuestion.getCollection_id());
        collection.getQuestionList().add(newQuestion);
        Collection newCollection = collectionRepo.save(collection);
        collectionService.updateRI(null, newCollection);

        return newQuestion;
    }

    public boolean delete(String id) {
        Question toDelete = questionRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
        questionRepo.deleteById(id);

        Collection oldCollection = collectionRepo.findCollectionById(toDelete.getCollection_id());
        Collection collection = collectionRepo.findCollectionById(toDelete.getCollection_id());
        collection.getQuestionList().remove(toDelete);
        Collection newCollection = collectionRepo.save(collection);
        collectionService.updateRI(oldCollection, newCollection);

        return true;
    }

    public Question findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidRequestException("Invalid id provided");
        }

        return questionRepo.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
    }

    public Question update(Question question) {
        /*
            Update each field, CANNOT UPDATE ID or COLLECTION_ID
            ID and Collection_id are immutable
         */

        Question oldQuestion = questionRepo.findById(question.getId()).orElseThrow(ResourceNotFoundException::new);
        Question temp = questionRepo.findById(question.getId()).orElseThrow(ResourceNotFoundException::new);

        temp.setQuestion(question.getQuestion());
        temp.setAnswer(question.getAnswer());
        temp.setCategory(question.getCategory());
        temp.setValue(question.getValue());
        temp.setPenaltyValue(question.getPenaltyValue());
        temp.setMultiplier(question.getMultiplier());
        temp.setHints(question.getHints());

        //Update Question
        Question saved = questionRepo.save(temp);

        //Update Collection
        Collection oldCollection = collectionService.findCollectionById(oldQuestion.getCollection_id());
        Collection owner = collectionService.findCollectionById(oldQuestion.getCollection_id());
        List<Question> questions = owner.getQuestionList();
        for (Question q : questions) {
            if(q.getId().equals(oldQuestion.getId())) {
                questions.remove(q);
                questions.add(temp);
            }
        }
        owner.setQuestionList(questions);
        Collection updatedCollection = collectionRepo.save(owner);
        collectionService.updateRI(oldCollection, updatedCollection);

        return saved;
    }

}
