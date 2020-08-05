package org.tamal.spring;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.tamal.spring.service.PersonService;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class PersonTest {

    @Autowired
    private PersonService personService;

    @Test
    void testGetPersonsWithoutPageAndOrder() {
        assertNotNull(personService.getPersons(null, Pageable.unpaged()));
    }
}
