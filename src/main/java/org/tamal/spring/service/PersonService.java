package org.tamal.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.tamal.spring.dao.PersonRepository;
import org.tamal.spring.model.Person;

import java.util.Optional;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public Page<Person> getPersons(Specification<Person> specification, Pageable pageable) {
        return personRepository.findAll(specification, pageable);
    }

    public Optional<Person> getPerson(int id) {
        return personRepository.findById(id);
    }

    public Person save(Person person) {
        return personRepository.save(person);
    }

    public void delete(int id) {
        personRepository.deleteById(id);
    }

}
