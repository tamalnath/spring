package org.tamal.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.tamal.spring.dao.PersonRepository;
import org.tamal.spring.model.Person;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public Page<Person> getPersons(int page, int size, List<String> orders, String direction) {
        if (page < 0 || size <= 0) {
            return personRepository.findAll(Pageable.unpaged());
        }
        Sort.Direction dir = Sort.Direction.fromOptionalString(direction).orElse(Sort.Direction.ASC);
        Sort sort = (orders == null || orders.isEmpty()) ? Sort.unsorted() : Sort.by(dir, orders.toArray(new String[0]));
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return personRepository.findAll(pageRequest);
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
