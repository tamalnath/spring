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
import org.tamal.spring.model.Page;
import org.tamal.spring.model.Person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    public void getPersons() {
        ResponseEntity<Page<Person>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, pagedPersonType);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Page<Person> pagedPerson = responseEntity.getBody();
        assertNotNull(pagedPerson);
        assertFalse(pagedPerson.empty);
        assertNotNull(pagedPerson.content);
        assertFalse(pagedPerson.content.isEmpty());
    }

}
