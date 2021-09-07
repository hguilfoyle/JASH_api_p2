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

    public Question createNewQuestion(Question newQuestion) {
        if(!isValid(newQuestion)) {
            throw new InvalidRequestException("Invalid Question");
        }

        newQuestion = questionRepo.save(newQuestion);

        collectionService.addQuestionToCollection(newQuestion);

        return newQuestion;
    }

    public Question findQuestionById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidRequestException("Invalid id provided");
        }

        return questionRepo.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
    }

    public void deleteById(String id) {
        Question toDelete = questionRepo.findById(id).orElseThrow(ResourceNotFoundException::new);

        Collection owner = collectionRepo.findCollectionById(toDelete.getCollection_id());
        List<Question> questions = owner.getQuestionList();
        questions.remove(toDelete);
        owner.setQuestionList(questions);
        collectionRepo.save(owner);

        User author = userRepo.findById(owner.getAuthor().getId()).orElseThrow(ResourceNotFoundException::new);
        System.out.println(author);
        List<Collection> collections = author.getCollections();
        for(Collection c : collections) {
            c.getQuestionList().remove(toDelete);
        }
        author.setCollections(collections);
        userRepo.save(author);

        List<User> usersWithQuestion = userRepo.findByFavoritesContaining(toDelete); //Need to test this line!!!
        for(User u : usersWithQuestion) {
            collections = u.getFavorites();
            for(Collection c : collections) {
                c.getQuestionList().remove(toDelete);
            }
            u.setFavorites(collections);
            userRepo.save(u);
        }

        //Finally, delete the question.
        questionRepo.deleteById(id);
    }

    public Question update(Question question) {
        Question toSave = questionRepo.findById(question.getId()).orElseThrow(ResourceNotFoundException::new);

        /*
            Update each field, CANNOT UPDATE ID or COLLECTION_ID
            ID and Collection_id are immutable
         */

        toSave.setQuestion(question.getQuestion());
        toSave.setAnswer(question.getAnswer());
        toSave.setCategory(question.getCategory());
        toSave.setValue(question.getValue());
        toSave.setPenaltyValue(question.getPenaltyValue());
        toSave.setMultiplier(question.getMultiplier());
        toSave.setHints(question.getHints());

        return questionRepo.save(question);
    }

    //TODO: Implement me
    private boolean isValid(Question question) {
        return true;
    }
}
