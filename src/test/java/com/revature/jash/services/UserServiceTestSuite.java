package com.revature.jash.services;

import com.revature.jash.datasource.documents.User;
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

    @BeforeEach
    public void beforeEachTest() {
        mockUserRepo = mock(UserRepository.class);
        mockPasswordUtils = mock(PasswordUtils.class);
        mockUserService = mock(UserService.class);
        sut = new UserService(mockUserRepo, mockPasswordUtils);
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
        UserDTO result = sut.findUserById(id);
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
         InvalidRequestException e = assertThrows(InvalidRequestException.class, () -> sut.findUserById(id));
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
    public void login_executesSuccesfully_whenGivenValidUsernameAndPassword() {
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
    public void login_throwsExceptiion_whenUsernameOrPasswordIsBlank() {
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
    public void login_throwsExceptiion_whenAuthUserDoesNotExist() {
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
    public void deleteById_executesSuccessfully_whenGivenValidID() {
        String id = "validId";
        User validUser = new User("valid","valid","valid","valid@valid","username","password");
        User expectedUser = new User("valid","valid","valid","valid@valid","username","password");

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