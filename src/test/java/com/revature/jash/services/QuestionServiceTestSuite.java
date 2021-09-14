package com.revature.jash.services;

import com.revature.jash.datasource.documents.Collection;
import com.revature.jash.datasource.documents.Question;
import com.revature.jash.datasource.documents.User;
import com.revature.jash.datasource.repositories.CollectionRepository;
import com.revature.jash.datasource.repositories.QuestionRepository;
import com.revature.jash.datasource.repositories.UserRepository;
import com.revature.jash.util.exceptions.InvalidRequestException;
import com.revature.jash.web.dtos.Principal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class QuestionServiceTestSuite {

    QuestionService sut;

    private QuestionRepository mockQuestionRepo;
    private CollectionService mockCollectionService;
    private CollectionRepository mockCollectionRepo;
    private UserRepository mockUserRepo;

    @BeforeEach
    public void beforeEachTest(){
        mockQuestionRepo = mock(QuestionRepository.class);
        mockCollectionService = mock(CollectionService.class);
        mockCollectionRepo = mock(CollectionRepository.class);
        mockUserRepo = mock(UserRepository.class);
        sut = new QuestionService(mockQuestionRepo, mockCollectionService, mockCollectionRepo, mockUserRepo);
    }

    @AfterEach
    public void afterEachTest() { sut = null; }

    @Test
    public void create_executesSuccesfully_whenGivenValidQuestion() {
        //Arrange

        //for collection
        User authUser = new User("valid","valid","valid@valid","username","password");
        Principal author = new Principal(authUser);
        List<Question> validQuestionList = new ArrayList<>();

        List<String> validList = new ArrayList<>();
        Question validQuestion = new Question("valid","valid","valid","valid","valid",1,2,1,validList);
        Collection validCollection = new Collection("valid","valid","valid",author,"valid",validQuestionList);

        when(mockQuestionRepo.save(validQuestion)).thenReturn(validQuestion);
        when(mockCollectionRepo.findCollectionById("valid")).thenReturn(validCollection);
        validCollection.getQuestionList().add(validQuestion);
        when(mockCollectionRepo.save(validCollection)).thenReturn(validCollection);

        //Act
        Question result = sut.create(validQuestion);
        //Assert
        Assertions.assertEquals(result, validQuestion);
        verify(mockQuestionRepo,times(1)).save(validQuestion);
        verify(mockCollectionRepo, times(1)).findCollectionById(anyString());
        verify(mockCollectionRepo,times(1)).save(validCollection);
    }

    @Test
    public void delete_executesSuccessfully_whenGivenValidId() {
        //Arrange
        //for collection
        User authUser = new User("valid","valid","valid@valid","username","password");
        Principal author = new Principal(authUser);
        List<Question> validQuestionList = new ArrayList<>();

        String id = "valid";
        List<String> validList = new ArrayList<>();
        Question validQuestion = new Question(id,"valid","valid","valid","valid",1,2,1,validList);
        Collection validCollection = new Collection("valid","valid","valid",author,"valid",validQuestionList);

        when(mockQuestionRepo.findById("valid")).thenReturn(java.util.Optional.of(validQuestion));

        when(mockCollectionRepo.findCollectionById("valid")).thenReturn(validCollection);
        validCollection.getQuestionList().remove(validQuestion);
        when(mockCollectionRepo.save(validCollection)).thenReturn(validCollection);
        //Act
        sut.delete(id);
        //Assert
        verify(mockQuestionRepo,times(1)).findById(id);
        verify(mockCollectionRepo, times(1)).findCollectionById(anyString());
        verify(mockCollectionRepo,times(1)).save(validCollection);
    }

    @Test
  public void findById_successfullyReturns_whenGivenValidId() {
        //Arrange
        String id = "valid";
        List<String> validList = new ArrayList<>();
        Question existingQuestion = new Question(id,"","","","",1,2,1,validList);
        when(mockQuestionRepo.findById(id)).thenReturn(java.util.Optional.of(existingQuestion));
        //Act
        Question result = sut.findById(id);
        //Assert
        Assertions.assertEquals(result,existingQuestion);
        verify(mockQuestionRepo, times(1)).findById(id);
    }

    @Test
    public void findById_throwsException_whenFieldIsBlank() {
        //Arrange
        String id = "";
        List<String> validList = new ArrayList<>();
        Question existingQuestion = new Question(id,"","","","",1,2,1,validList);
        when(mockQuestionRepo.findById(id)).thenReturn(java.util.Optional.of(existingQuestion));
        //Act
        InvalidRequestException e = assertThrows(InvalidRequestException.class, () -> sut.findById(id));
        //Assert
        assertEquals("Invalid id provided", e.getMessage());

    }

    @Test
    public void update_executeSuccessfully_whenGivenValidQuestion() {
        //Arrange
        //for collection
        User authUser = new User("valid","valid","valid@valid","username","password");
        Principal author = new Principal(authUser);
        List<Question> validQuestionList = new ArrayList<>();

        String id = "valid";
        List<String> validList = new ArrayList<>();
        Question validOldQuestion = new Question(id,"valid","valid","valid","valid",1,2,1,validList);
        Question validNewQuestion = new Question(id,"valid","valid2","valid2","valid2",1,2,1,validList);
        Collection validCollection = new Collection("valid","valid","valid",author,"valid",validQuestionList);
        Collection validNewCollection = new Collection("valid","valid","valid",author,"valid",validQuestionList);

        when(mockQuestionRepo.findById(id)).thenReturn(java.util.Optional.of(validOldQuestion));
        validNewQuestion.setQuestion(validOldQuestion.getQuestion());
        validNewQuestion.setAnswer(validOldQuestion.getAnswer());
        validNewQuestion.setCategory(validOldQuestion.getCategory());
        validNewQuestion.setValue(validOldQuestion.getValue());
        validNewQuestion.setPenaltyValue(validOldQuestion.getPenaltyValue());
        validNewQuestion.setMultiplier(validOldQuestion.getMultiplier());
        validNewQuestion.setHints(validOldQuestion.getHints());

        when(mockQuestionRepo.save(validNewQuestion)).thenReturn(validNewQuestion);
        when(mockCollectionService.findCollectionById(validOldQuestion.getCollection_id()))
                .thenReturn(validCollection);
        validCollection.setQuestionList(validQuestionList);
        when(mockCollectionRepo.save(validCollection)).thenReturn(validNewCollection);
        //Act

        Question result = sut.update(validNewQuestion);
        //Assert

        Assertions.assertEquals(result, validNewQuestion);
        verify(mockQuestionRepo, times(2)).findById(id);
        verify(mockQuestionRepo, times(1)).save(validNewQuestion);
        verify(mockCollectionService, times(1)).findCollectionById(validOldQuestion.getCollection_id());
        verify(mockCollectionRepo, times(1)).save(validCollection);
    }
}