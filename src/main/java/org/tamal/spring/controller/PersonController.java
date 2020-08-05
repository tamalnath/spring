package org.tamal.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.tamal.spring.model.Person;
import org.tamal.spring.service.PersonService;

import java.math.BigInteger;
import java.time.LocalDate;

@Controller
public class PersonController {

    @Autowired
    private PersonService personService;

    /**
     * Lists all the persons.
     * @param pageable the pagination and sorting parameters
     * @param name the name to search (accepts SQL like operator)
     * @param olderThan filter by date of birth older than the given date
     * @param youngerThan filter by date of birth younger than the given date
     * @param sex filter by sex ('M' or 'F')
     * @param email search by email address
     * @param phone search by phone number
     * @param address search by address (accepts SQL like operator)
     * @return persons matching the search criteria
     */
    @GetMapping("/persons")
    @ResponseBody
    private Page<Person> getPersons(Pageable pageable,
                                    @RequestParam(value = "name", required = false) String name,
                                    @RequestParam(value = "olderThan", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate olderThan,
                                    @RequestParam(value = "youngerThan", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate youngerThan,
                                    @RequestParam(value = "sex", required = false) Character sex,
                                    @RequestParam(value = "email", required = false) String email,
                                    @RequestParam(value = "phone", required = false) BigInteger phone,
                                    @RequestParam(value = "address", required = false) String address) {
        Specification<Person> specification = Specification.where(null);
        if (name != null) {
            specification = specification.and((root, query, builder) -> builder.like(root.get("name"), name));
        }
        if (olderThan != null && youngerThan != null) {
            specification = specification.and((root, query, builder) -> builder.between(root.get("dateOfBirth"), olderThan, youngerThan));
        } else if (olderThan != null) {
            specification = specification.and((root, query, builder) -> builder.greaterThanOrEqualTo(root.get("dateOfBirth"), olderThan));
        } else if (youngerThan != null) {
            specification = specification.and((root, query, builder) -> builder.lessThanOrEqualTo(root.get("dateOfBirth"), youngerThan));
        }
        if (sex != null) {
            specification = specification.and((root, query, builder) -> builder.equal(root.get("sex"), Character.toUpperCase(sex)));
        }
        if (email != null) {
            specification = specification.and((root, query, builder) -> builder.equal(root.get("email"), email));
        }
        if (phone != null) {
            specification = specification.and((root, query, builder) -> builder.equal(root.get("phone"), phone));
        }
        if (address != null) {
            specification = specification.and((root, query, builder) -> builder.like(root.get("address"), address));
        }
        return personService.getPersons(specification, pageable);
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
