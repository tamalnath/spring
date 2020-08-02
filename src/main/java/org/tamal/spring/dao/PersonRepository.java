package org.tamal.spring.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.tamal.spring.model.Person;

import java.time.LocalDate;

public interface PersonRepository extends PagingAndSortingRepository<Person, Integer> {

    Page<Person> findByName(String name, Pageable pageable);

    Page<Person> findByDateOfBirth(LocalDate dateOfBirth, Pageable pageable);

    Page<Person> findBySex(char sex, Pageable pageable);

    Page<Person> findByEmail(String email, Pageable pageable);

    Page<Person> findByPhone(String phone, Pageable pageable);

}
