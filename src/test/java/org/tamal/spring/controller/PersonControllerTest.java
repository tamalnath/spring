package org.tamal.spring.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import org.tamal.spring.model.Page;
import org.tamal.spring.model.Person;

import java.math.BigInteger;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PersonControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static String url;
    private final ParameterizedTypeReference<Page<Person>> pagedPersonType = new ParameterizedTypeReference<Page<Person>>() {};

    @BeforeEach
    private void setUp() {
        url = "http://localhost:" + port + "/persons";
    }

    @Test
    public void testCRUD() {
        int personId = createPerson();
        Person person = readPerson(personId);
        person = updatePerson(person);
        deletePerson(personId);
        searchPerson(person);
    }

    private void deletePerson(int personId) {
        restTemplate.delete(url + '/' + personId);
    }

    private int createPerson() {
        Person person = new Person();
        person.setName("Test User");
        person.setAddress("No 123, Some Street, Kolkata, West Bengal, India PIN: 700085");
        person.setDateOfBirth(LocalDate.of(2000, 1, 1));
        person.setGender('T');
        person.setEmail("test@test.com");
        person.setPhone(new BigInteger("9999999999"));
        ResponseEntity<? extends Person> responseEntity = restTemplate.postForEntity(url, person, person.getClass());
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Person savedPerson = responseEntity.getBody();
        assertNotNull(savedPerson);
        return savedPerson.getId();
    }

    private Person readPerson(int personId) {
        ResponseEntity<Person> responseEntity = restTemplate.getForEntity(url + '/' + personId, Person.class);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Person person = responseEntity.getBody();
        assertNotNull(person);
        return person;
    }

    private Person updatePerson(Person person) {
        person.setName("Updated User");
        ResponseEntity<? extends Person> responseEntity = restTemplate.postForEntity(url, person, person.getClass());
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Person savedPerson = responseEntity.getBody();
        assertNotNull(savedPerson);
        assertEquals(person.getName(), savedPerson.getName());
        return savedPerson;
    }

    private void searchPerson(Person person) {
        String query = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("email", person.getEmail())
                .queryParam("phone", person.getPhone())
                .build().toUriString();
        ResponseEntity<Page<Person>> responseEntity = restTemplate.exchange(query, HttpMethod.GET, null, pagedPersonType);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Page<Person> pagedPerson = responseEntity.getBody();
        assertNotNull(pagedPerson);
        assertTrue(pagedPerson.empty);
        assertNotNull(pagedPerson.content);
        assertTrue(pagedPerson.content.isEmpty());
    }

}
