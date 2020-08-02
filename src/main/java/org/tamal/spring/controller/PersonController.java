package org.tamal.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.tamal.spring.model.Person;
import org.tamal.spring.service.PersonService;

import java.util.List;

@Controller
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping("/persons")
    @ResponseBody
    private Page<Person> getPersons(Pageable pageable) {
        return personService.getPersons(pageable);
    }

    @GetMapping("/persons/{id}")
    @ResponseBody
    private Person getPerson(@PathVariable("id") int id) {
        return personService.getPerson(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/persons/{id}")
    private void deletePerson(@PathVariable("id") int id) {
        personService.delete(id);
    }

    @PostMapping("/persons")
    @ResponseBody
    private int savePerson(@RequestBody Person person) {
        personService.saveOrUpdate(person);
        return person.getId();
    }

}
