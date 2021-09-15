package com.revature.jash.services;

import com.revature.jash.datasource.documents.Collection;
import com.revature.jash.datasource.documents.Question;
import com.revature.jash.datasource.documents.User;
import com.revature.jash.datasource.repositories.CollectionRepository;
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
import static org.mockito.Mockito.times;

public class CollectionServiceTestSuite {

    CollectionService sut;

    private CollectionRepository mockCollectionRepo;
    private UserRepository mockUserRepository;
    private UserService mockUserService;
    private CollectionService mockCollectionService;


    @BeforeEach()
        public void beforeEachTest(){
            mockCollectionRepo = mock(CollectionRepository.class);
            mockUserRepository = mock(UserRepository.class);
            mockUserService = mock(UserService.class);
            mockCollectionService = mock(CollectionService.class);
            sut = new CollectionService(mockCollectionRepo, mockUserService, mockUserRepository);
        }


    @AfterEach()
        public void afterEachTest() { sut = null; }


    @Test
    public void findAll_successfullyReturns_collections() {
       //Arrange
        List<Collection> returnedList = new ArrayList<>();

        when(mockCollectionRepo.findAll()).thenReturn(returnedList);
        //Act
        List<Collection> actualList = sut.findAll();
        //Assert
        Assertions.assertEquals(actualList, returnedList);
    }


    @Test
    public void create_executesSuccessfully_whenGivenValidCollection() {
        //Arrange
        User authUser = new User("valid","valid","valid@valid","username","password");
        Principal author = new Principal(authUser);
        List<Question> validQuestionList = new ArrayList<>();

        String collectionId = "valid";
        Collection validCollection = new Collection(collectionId,"valid","valid",author,"valid",validQuestionList);
        Collection expectedCollection = new Collection(collectionId,"valid","valid",author,"valid",validQuestionList);
        mockCollectionService.isValid(validCollection);
        mockCollectionService.isUnique(validCollection);
        when(mockCollectionRepo.save(validCollection)).thenReturn(validCollection);

        //Act
        Collection result = sut.create(validCollection);
        //Assert
        Assertions.assertEquals(result,expectedCollection);
        verify(mockCollectionRepo,times(1)).save(validCollection);

    }

    @Test
    public void create_throwsException_whenFieldsLeftBlank() {
        //Arrange
        User authUser = new User("valid","valid","valid@valid","username","password");
        Principal author = new Principal(authUser);
        List<Question> validQuestionList = new ArrayList<>();

        String collectionId = "valid";
        Collection validCollection = new Collection(collectionId,"","valid",author,"valid",validQuestionList);
        Collection expectedCollection = new Collection(collectionId,"valid","valid",author,"valid",validQuestionList);
        mockCollectionService.isValid(validCollection);

        //Act
        InvalidRequestException e = assertThrows(InvalidRequestException.class,() -> sut.create(validCollection));
        //Assert
        assertEquals("Invalid Collection", e.getMessage());
    }

    @Test
    public void delete() {
        //Arrange
        User authUser = new User("valid","valid","valid@valid","username","password");
        Principal author = new Principal(authUser);
        List<Question> validQuestionList = new ArrayList<>();

        String collectionId = "valid";
        Collection validCollection = new Collection(collectionId,"valid","valid",author,"valid",validQuestionList);
        List<Collection> expectedCollections = new ArrayList<>();
        User validUser = new User("valid","valid","valid","valid@valid","username","password");

        when(mockCollectionRepo.findCollectionById(collectionId)).thenReturn(validCollection);
        when(mockUserRepository.findById(validUser.getId())).thenReturn(java.util.Optional.of(validUser));
        List<Collection> collections = validUser.getCollections();
        collections.remove(expectedCollections);
        validUser.setCollections(collections);
        when(mockUserRepository.save(validUser)).thenReturn(validUser);

        //Act
        sut.delete(collectionId);

        //Assert
        verify(mockCollectionRepo, times(1)).findCollectionById(collectionId);
        verify(mockUserRepository,times(1)).findById(validUser.getId());
        verify(mockUserRepository, times(1)).save(validUser);
    }

    @Test
    public void update() {
        //Arrange
        User authUser = new User("valid","valid","valid@valid","username","password");
        Principal author = new Principal(authUser);
        List<Question> validQuestionList = new ArrayList<>();

        String collectionId = "valid";
        Collection validOldCollection = new Collection(collectionId,"valid","valid",author,"valid",validQuestionList);
        Collection validNewCollection = new Collection(collectionId,"valid2","valid2",author,"valid",validQuestionList);
        User validUser = new User("valid","valid","valid","valid@valid","username","password");

        mockCollectionService.isValid(validOldCollection);
        when(mockCollectionRepo.findCollectionById(validOldCollection.getId())).thenReturn(validOldCollection);

        validNewCollection.setCategory(validOldCollection.getCategory());
        validNewCollection.setDescription(validOldCollection.getDescription());
        validNewCollection.setTitle(validOldCollection.getTitle());

        when(mockCollectionRepo.save(validNewCollection)).thenReturn(validNewCollection);
        mockCollectionService.updateRI(validOldCollection, validNewCollection);
        when(mockUserRepository.findById(validUser.getId())).thenReturn(java.util.Optional.of(validUser));

        //Act
        Collection result = sut.update(validNewCollection);

        //Assert
        Assertions.assertEquals(result,validNewCollection);
        verify(mockCollectionRepo, times(2)).findCollectionById(collectionId);
        verify(mockCollectionRepo, times(1)).save(validNewCollection);
        verify(mockUserRepository, times(1)).findById(validUser.getId());
    }

    @Test
    public void updateRI() {
        //Arrange
        User authUser = new User("valid","valid","valid@valid","username","password");
        Principal author = new Principal(authUser);
        List<Question> validQuestionList = new ArrayList<>();

        String collectionId = "valid";
        Collection validCollection = new Collection(collectionId,"valid","valid",author,"valid",validQuestionList);;
        User validUser = new User("valid","valid","valid","valid@valid","username","password");

        when(mockUserRepository.findById(validUser.getId())).thenReturn(java.util.Optional.of(validUser));
        when(mockUserRepository.save(validUser)).thenReturn(validUser);
        //Act
        sut.updateRI(validCollection, validCollection);

        //Assert
        verify(mockUserRepository, times(1)).findById(validUser.getId());
        verify(mockUserRepository, times(1)).save(validUser);
    }

    @Test
    public void isValid_executesSuccessfully_whenGivenValidUser(){
        //Arrange
        User authUser = new User("valid","valid","valid@valid","username","password");
        Principal author = new Principal(authUser);
        List<Question> validQuestionList = new ArrayList<>();
        Collection validCollection = new Collection("valid","valid","valid",author,"valid",validQuestionList);

        //Act
        sut.isValid(validCollection);
        //Assert
        Assertions.assertTrue(true);
    }

    @Test
    public void isUnique() {
        //Arrange


        //Act

        //Assert
    }

    @Test
    public void findCollectionById_ReturnsSuccessfully_allCollections() {
        //for collection
        User authUser = new User("valid","valid","valid@valid","username","password");
        Principal author = new Principal(authUser);
        List<Question> validQuestionList = new ArrayList<>();

        String id = "valid";
        List<String> validList = new ArrayList<>();
        Collection existingCollection = new Collection(id,"valid","valid",author,"valid",validQuestionList);
        when(mockCollectionRepo.findById(id)).thenReturn(java.util.Optional.of(existingCollection));
        //Act
        Collection result = sut.findCollectionById(id);
        //Assert
        Assertions.assertEquals(result,existingCollection);
        verify(mockCollectionRepo, times(1)).findById(id);

    }
    @Test
    public void findCollectionById_throwsException_whenIdIsBlank() {
        //for collection
        User authUser = new User("valid","valid","valid@valid","username","password");
        Principal author = new Principal(authUser);
        List<Question> validQuestionList = new ArrayList<>();

        String id = "";
        List<String> validList = new ArrayList<>();
        Collection existingCollection = new Collection(id,"valid","valid",author,"valid",validQuestionList);
        when(mockCollectionRepo.findById(id)).thenReturn(java.util.Optional.of(existingCollection));
        //Act
        InvalidRequestException e = assertThrows(InvalidRequestException.class, () -> sut.findCollectionById(id));
        //Assert
        assertEquals("Invalid id provided", e.getMessage());
    }
}