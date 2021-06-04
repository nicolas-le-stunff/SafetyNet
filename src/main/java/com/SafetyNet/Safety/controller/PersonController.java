package com.SafetyNet.Safety.controller;


import com.SafetyNet.Safety.service.PersonService;
import com.SafetyNet.Safety.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class PersonController {

    @Autowired
    private PersonService personService;

    /*
    Récuperation d'une liste de Person
    */
    @GetMapping(value = "/personsInfo")
    public List<Person> listePersons(){ return personService.findAll(); }

    @PostMapping(value = "/person")
    public void personPost(){
        //TODO
    }

    @PutMapping(value = "/person")
    public void putPerson(){
        //TODO
    }

    @DeleteMapping(value = "/person")
    public void personDelete(){
        //TODO
    }







    public void addPerson(@RequestBody Person person){ personService.personSave(person); }







}
