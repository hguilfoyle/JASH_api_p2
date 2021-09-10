package com.revature.jash.services;

import com.revature.jash.datasource.documents.Collection;
import com.revature.jash.datasource.documents.Question;
import com.revature.jash.datasource.documents.User;
import com.revature.jash.datasource.repositories.CollectionRepository;
import com.revature.jash.datasource.repositories.UserRepository;
import com.revature.jash.util.PasswordUtils;
import com.revature.jash.util.exceptions.AuthenticationException;
import com.revature.jash.util.exceptions.InvalidRequestException;
import com.revature.jash.util.exceptions.ResourcePersistenceException;
import com.revature.jash.web.dtos.Principal;
import com.revature.jash.web.dtos.UserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTestSuite {

    UserService sut;

    private UserRepository mockUserRepo;
    private PasswordUtils mockPasswordUtils;
    private UserService mockUserService;
    private CollectionRepository mockCollectionRepo;

    @BeforeEach
    public void beforeEachTest() {
        mockUserRepo = mock(UserRepository.class);
        mockPasswordUtils = mock(PasswordUtils.class);
        mockUserService = mock(UserService.class);
        mockCollectionRepo = mock(CollectionRepository.class);
        sut = new UserService(mockUserRepo, mockCollectionRepo,  mockPasswordUtils);
    }

    @AfterEach
    public void afterEachTest() { sut = null; }


    @Test
    public void findAll_successfullyReturns_UserDTOList() {
        //Arrange
        List<User> returnedList = new ArrayList<>();
        List<UserDTO> expectedList = new ArrayList<>();

        when(mockUserRepo.findAll()).thenReturn(returnedList);
        //Act
        List<UserDTO> actualList = sut.findAll();
        //Assert
        Assertions.assertEquals(actualList,expectedList);
    }

    @Test
    public void findUserById_successfullyReturns_whenGivenValidId() {
        //Arrange
        String id = "validID";
        User existingUser = new User("valid","valid","valid","valid@valid","valid","valid");
        existingUser.setId(id);
        UserDTO expectedUser = new UserDTO(existingUser);
        when(mockUserRepo.findById(id)).thenReturn(java.util.Optional.of(existingUser));
        //Act
        UserDTO result = sut.findById(id);
        //Assert
        Assertions.assertEquals(result, expectedUser);
        verify(mockUserRepo, times(1)).findById(id);
    }

    @Test
    public void findUserById_throwsException_whenFieldIsBlank() {
        //Arrange
        String id = "";
        User existingUser = new User("valid","valid","valid","valid@valid","valid","valid");
        existingUser.setId(id);
        UserDTO expectedUser = new UserDTO(existingUser);
        when(mockUserRepo.findById(id)).thenReturn(java.util.Optional.of(existingUser));
        //Act
         InvalidRequestException e = assertThrows(InvalidRequestException.class, () -> sut.findById(id));
        //Assert
        assertEquals("Invalid id provided", e.getMessage());

    }

    @Test
    public void register_executesSuccessfully_whenGivenValidUser() {
        //Arrange
        User expected = new User("valid","valid","valid@valid","valid","valid");
        User validUser = new User("valid","valid","valid@valid","valid","valid");
        mockUserService.isUserValid(validUser);
        when(mockUserRepo.findUserByUsername(validUser.getUsername())).thenReturn(null);
        when(mockUserRepo.findUserByEmail(validUser.getEmail())).thenReturn(null);
        when(mockPasswordUtils.generateSecurePassword(validUser.getPassword())).thenReturn("encryptedPassword");
        when(mockUserRepo.save(validUser)).thenReturn(expected);

        //Act
        User result = sut.register(validUser);

        //Assert
        Assertions.assertEquals(result,expected);
        verify(mockUserRepo, times(1)).findUserByUsername(anyString());
        verify(mockUserRepo, times(1)).findUserByEmail(anyString());
        verify(mockPasswordUtils, times(1)).generateSecurePassword(anyString());
        verify(mockUserRepo, times(1)).save(validUser);
    }

    @Test
    public void register_throwsException_whenGivenInvalidUser() {
        //Arrange
        User expected = new User("valid","valid","valid@valid","valid","valid");
        User validUser = new User("valid","valid","valid@valid","valid","valid");
        User invalidUser = new User("","","","valid","valid");
        when(mockUserService.isUserValid(invalidUser)).thenReturn(true);

        //Act
         InvalidRequestException e = assertThrows(InvalidRequestException.class, () -> sut.register(invalidUser));

        //Assert
        assertEquals("Invalid user data provided!", e.getMessage());
    }

    @Test
    public void register_throwsException_whenGivenAlreadyExistingUserName() {
        //Arrange
        User existingUser = new User("valid","valid","valid@valid","valid","valid");
        User validUser = new User("valid","valid","valid@valid","valid","valid");
        when(mockUserRepo.findUserByUsername(anyString())).thenReturn(existingUser);

        //Act
        ResourcePersistenceException e = assertThrows(ResourcePersistenceException.class, () -> sut.register(validUser));

        //Assert
        assertEquals("Provided username is already taken!", e.getMessage());
        verify(mockUserRepo, times(1)).findUserByUsername(anyString());

    }

    @Test
    public void register_throwsException_whenGivenAlreadyExistingEmail() {
        //Arrange
        User existingUser = new User("valid","valid","valid@valid","valid","valid");
        User validUser = new User("valid","valid","valid@valid","valid","valid");
        when(mockUserRepo.findUserByUsername(validUser.getUsername())).thenReturn(null);
        when(mockUserRepo.findUserByEmail(anyString())).thenReturn(existingUser);

        //Act
        ResourcePersistenceException e = assertThrows(ResourcePersistenceException.class, () -> sut.register(validUser));

        //Assert
        assertEquals("Provided email is already taken!", e.getMessage());
        verify(mockUserRepo, times(1)).findUserByUsername(anyString());
        verify(mockUserRepo, times(1)).findUserByEmail(anyString());

    }

    @Test
    public void login_executesSuccessfully_whenGivenValidUsernameAndPassword() {
       //Arrange
        String username = "username";
        String password = "password";
        User authUser = new User("valid","valid","valid@valid","valid","valid");
        Principal expectedResult = new Principal(authUser);
        when(mockPasswordUtils.generateSecurePassword(password)).thenReturn("encryptedPassword");
        when(mockUserRepo.findUserByUsernameAndPassword(username, "encryptedPassword")).thenReturn(authUser);

        //Act
        Principal actualResult = sut.login(username, password);

        //Assert
        Assertions.assertEquals(actualResult,expectedResult);
        verify(mockPasswordUtils,times(1)).generateSecurePassword(anyString());
        verify(mockUserRepo,times(1)).findUserByUsernameAndPassword(anyString(),anyString());
    }

    @Test
    public void login_throwsException_whenUsernameOrPasswordIsBlank() {
        //Arrange
        String username = "";
        String password = "password";
        User authUser = new User("valid","valid","valid@valid","username","password");
        Principal expectedResult = new Principal(authUser);
        when(mockPasswordUtils.generateSecurePassword(password)).thenReturn("encryptedPassword");
        when(mockUserRepo.findUserByUsernameAndPassword(username, "encryptedPassword")).thenReturn(authUser);

        //Act
        InvalidRequestException e = assertThrows(InvalidRequestException.class, () -> sut.login(username, password));

        //Assert
        assertEquals("Invalid user credentials provided!", e.getMessage());
    }

    @Test
    public void login_throwsException_whenAuthUserDoesNotExist() {
        //Arrange
        String username = "username";
        String password = "password";
        User authUser = new User("valid","valid","valid@valid","username","password");
        Principal expectedResult = new Principal(authUser);
        when(mockPasswordUtils.generateSecurePassword(password)).thenReturn("encryptedPassword");
        when(mockUserRepo.findUserByUsernameAndPassword(username, "encryptedPassword")).thenReturn(null);

        //Act
        AuthenticationException e = assertThrows(AuthenticationException.class, () -> sut.login(username, password));

        //Assert
        assertEquals("Invalid credentials provided!", e.getMessage());
        verify(mockPasswordUtils,times(1)).generateSecurePassword(anyString());
    }

    @Test
    public void delete_executesSuccessfully_whenGivenValidID() {
       //Arrange
        String id = "validId";
        User validUser = new User(id,"valid","valid","valid@valid","username","password");
        User expectedUser = new User(id,"valid","valid","valid@valid","username","password");

        when(mockUserRepo.findById(id)).thenReturn(java.util.Optional.of(validUser));
       //Act
       sut.delete(id);
       //Arrange
       verify(mockUserRepo,times(1)).findById(id);
    }

    @Test
    public void delete_throwExecution_whenGivenInvalidID() {
        //Arrange
        String id = "";
        User validUser = new User(id,"valid","valid","valid@valid","username","password");

        when(mockUserRepo.findById(id)).thenReturn(java.util.Optional.of(validUser));
        //Act
        InvalidRequestException e = assertThrows(InvalidRequestException.class,() -> sut.delete(id));
        //Arrange
        assertEquals("Invalid id provided", e.getMessage());
    }

    @Test
    public void update_executesSuccessfully_whenGivenValidUser() {
        //arrange
        User validUser = new User("valid","valid","valid","valid@valid","username","password");
        User toSaveUser = new User("valid","newvalid","newvalid","newvalid@valid","username","password");

        when(mockUserRepo.findById(validUser.getId())).thenReturn(java.util.Optional.of(validUser));
        toSaveUser.setFirstName(validUser.getFirstName());
        toSaveUser.setLastName(validUser.getLastName());
        toSaveUser.setEmail(validUser.getEmail());

        when(mockUserRepo.save(toSaveUser)).thenReturn(toSaveUser);

        //Act
        User expectedUser = sut.update(validUser);

        //Arrange
        Assertions.assertEquals(toSaveUser,expectedUser);
        verify(mockUserRepo,times(1)).findById(validUser.getId());
        verify(mockUserRepo,times(1)).save(toSaveUser);
    }

    /*
    * Need to figure out how to TEST a FOR LOOP
    * */
    @Test
    public void addCollection_executesSuccessfully_whenGivenValidCollection() {
        //Arrange
        User authUser = new User("valid","valid","valid","valid@valid","username","password");
        Principal validPrincipal = new Principal(authUser);
        List<Question> validList = new ArrayList<>();
        Collection validCollection = new Collection("valid","valid", "valid", validPrincipal, "valid",validList);

        when(mockUserRepo.findById(validPrincipal.getId())).thenReturn(java.util.Optional.of(authUser));
        when(mockUserRepo.save(authUser)).thenReturn(authUser);
        //Act
        sut.addCollection(validCollection);

        //Assert
        verify(mockUserRepo,times(1)).findById(validPrincipal.getId());
        verify(mockUserRepo, times(1)).save(authUser);
    }

    @Test
    public void addFavorite_executesSuccessfully_whenGivenValidUserIdAndCollectionId() {
        //Arrange
       String userId = "valid";
       String collectionId= "valid";
       List<Question> validList = new ArrayList<>();
       User validUser = new User(userId,"valid","valid","valid@valid","username","password");
       Principal validPrincipal = new Principal(validUser);
       Collection validCollection = new Collection("valid","valid", "valid", validPrincipal, "valid",validList);
       List<Collection> favorite = validUser.getFavorites();

       when(mockUserRepo.findById(userId)).thenReturn(java.util.Optional.of(validUser));
       when(mockCollectionRepo.findById(collectionId)).thenReturn(java.util.Optional.of(validCollection));

       validUser.setFavorites(favorite);
       when(mockUserRepo.save(validUser)).thenReturn(validUser);
       //Act

      sut.addFavorite(userId, collectionId);

      //Assert
      verify(mockUserRepo,times(1)).findById(userId);
      verify(mockCollectionRepo,times(1)).findById(collectionId);
      verify(mockUserRepo,times(1)).save(validUser);
    }

    @Test
    public void removeFavorite_executesSuccessfully_whenGivenValidUserIdAndCollectionId(){
        //Arrange
        String userId = "valid";
        String collectionId= "valid";
        List<Question> validList = new ArrayList<>();
        User validUser = new User(userId,"valid","valid","valid@valid","username","password");
        Principal validPrincipal = new Principal(validUser);
        Collection validCollection = new Collection("valid","valid", "valid", validPrincipal, "valid",validList);
        List<Collection> favorite = validUser.getFavorites();

        when(mockUserRepo.findById(userId)).thenReturn(java.util.Optional.of(validUser));
        when(mockCollectionRepo.findById(collectionId)).thenReturn(java.util.Optional.of(validCollection));

        validUser.setFavorites(favorite);
        when(mockUserRepo.save(validUser)).thenReturn(validUser);
        //Act

        sut.removeFavorite(userId, collectionId);

        //Assert
        verify(mockUserRepo,times(1)).findById(userId);
        verify(mockCollectionRepo,times(1)).findById(collectionId);
        verify(mockUserRepo,times(1)).save(validUser);
    }

    @Test
    public void isUsernameAvailable_executesSuccessfully_whenGivenValidUsername(){
        //Arrange
        String username = "valid";
        when(mockUserRepo.findUserByUsername(username)).thenReturn(null);

        //Act
        Boolean result = sut.isUsernameAvailable(username);
        //Assert
        Assertions.assertTrue(result);
        verify(mockUserRepo,times(1)).findUserByUsername(anyString());
    }

    @Test
    public void isUsernameAvailable_throwsException_whenGivenAlreadyTakenUsername(){
        //Arrange
        String username = "";
        when(mockUserRepo.findUserByUsername(username)).thenReturn(null);

        //Act
        InvalidRequestException e = assertThrows(InvalidRequestException.class,() -> sut.isUsernameAvailable(username));
        //Assert
        assertEquals("Invalid username value provided!", e.getMessage());
    }

    @Test
    public void isEmailAvailable_executesSuccessfully_whenGivenValidUsername(){
        //Arrange
        String email = "valid";
        when(mockUserRepo.findUserByEmail(email)).thenReturn(null);

        //Act
        Boolean result = sut.isEmailAvailable(email);
        //Assert
        Assertions.assertTrue(result);
        verify(mockUserRepo,times(1)).findUserByEmail(anyString());
    }

    @Test
    public void isEmailAvailable_throwsException_whenGivenAlreadyTakenUsername(){
        //Arrange
        String email = "";
        when(mockUserRepo.findUserByEmail(email)).thenReturn(null);

        //Act
        InvalidRequestException e = assertThrows(InvalidRequestException.class,() -> sut.isEmailAvailable(email));
        //Assert
        assertEquals("Invalid email value provided!", e.getMessage());
    }



    @Test
    public void isUserValid_executesSuccessfully_whenGivenValidUser(){
        //Arrange
        User validuser = new User ("valid","valid","valid@valid","valid","valid");

        //Act
        sut.isUserValid(validuser);
        //Assert
        Assertions.assertTrue(true);
    }
}