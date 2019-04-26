package org.tamal.spring.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.tamal.spring.model.Person;

public interface PersonRepository extends PagingAndSortingRepository<Person, Integer> {
}
