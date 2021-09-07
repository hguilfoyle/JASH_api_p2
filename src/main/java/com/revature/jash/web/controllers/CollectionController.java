package com.revature.jash.web.controllers;

import com.revature.jash.datasource.documents.Collection;
import com.revature.jash.datasource.documents.Question;
import com.revature.jash.services.CollectionService;
import com.revature.jash.services.QuestionService;
import com.revature.jash.util.exceptions.AuthenticationException;
import com.revature.jash.util.exceptions.UserForbiddenException;
import com.revature.jash.web.dtos.Principal;
import com.revature.jash.web.dtos.UserDTO;
import com.revature.jash.web.util.security.Secured;
import com.revature.jash.web.util.security.TokenParser;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/collections")
public class CollectionController {
    private final CollectionService collectionService;
    private final TokenParser parser;

    public CollectionController(CollectionService collectionService, TokenParser parser){
        this.collectionService = collectionService;
        this.parser = parser;
    }

    @GetMapping(produces = "application/json")
    @Secured(allowedUsers = {})
    public List<Collection> getAllCollections() {
        return collectionService.findAll();
    }

    @PostMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Collection createCollection(@RequestBody @Valid Collection newCollection){
        return collectionService.createNewCollection(newCollection);
    }

    @GetMapping(value = "{id}",produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public Collection findById(@PathVariable String id) {
        return collectionService.findCollectionById(id);
    }

    @PutMapping(produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public Collection replaceCollection(@RequestBody @Valid Collection updatedCollection, HttpServletRequest req) {
        Principal principal = parser.parseToken(req).orElseThrow(() -> new AuthenticationException("Request originates from an unauthenticated source."));
        String requester = principal.getId();
        String accessed = updatedCollection.getAuthor().getId();
        if(!requester.equals(accessed)) {
            throw new UserForbiddenException("Not allowed to update other Collections that you dont own");
        }

        return collectionService.replaceCollection(updatedCollection);
    }

    @DeleteMapping(value = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCollection(@PathVariable String id, HttpServletRequest req) {
        Principal principal = parser.parseToken(req).orElseThrow(() -> new AuthenticationException("Request originates from an unauthenticated source."));
        String requester = principal.getId();
        String accessed = collectionService.findCollectionById(id).getAuthor().getId();
        if(!requester.equals(accessed)) {
            throw new UserForbiddenException("Not allowed to delete Collections that you dont own");
        }

        collectionService.deleteById(id);
    }

}
