package org.tamal.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.tamal.spring.dao.PersonRepository;
import org.tamal.spring.model.Person;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public List<Person> getPersons(int page, int size, List<String> orders, String direction) {
        Sort.Direction dir = Sort.Direction.fromOptionalString(direction).orElse(Sort.Direction.ASC);
        Sort sort = (orders == null || orders.isEmpty()) ? Sort.unsorted() : Sort.by(dir, orders.toArray(new String[0]));
        Iterable<Person> iterable;
        if (size == 0) {
            iterable = personRepository.findAll(sort);
        } else {
            iterable = personRepository.findAll(PageRequest.of(page, size, sort));
        }
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    }

    public Optional<Person> getPerson(int id) {
        return personRepository.findById(id);
    }

    public void saveOrUpdate(Person person) {
        personRepository.save(person);
    }

    public void delete(int id) {
        personRepository.deleteById(id);
    }

}
