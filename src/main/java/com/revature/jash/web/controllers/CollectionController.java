package com.revature.jash.web.controllers;

import com.revature.jash.datasource.documents.Collection;
import com.revature.jash.datasource.documents.Question;
import com.revature.jash.services.CollectionService;
import com.revature.jash.services.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/collections")
public class CollectionController {
    private final CollectionService collectionService;
    public CollectionController(CollectionService collectionService){
        this.collectionService = collectionService;
    }

    @PostMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Collection createCollection(@RequestBody @Valid Collection newCollection){
        return collectionService.createNewCollection(newCollection);
    }

    @GetMapping(produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public Collection findById(@PathVariable String id) {
        return collectionService.findCollectionById(id);
    }

}
