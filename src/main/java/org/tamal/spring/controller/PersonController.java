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

import java.time.LocalDate;
import java.util.Objects;

@Controller
public class PersonController {

    @Autowired
    private PersonService personService;

    /**
     * Lists all the persons.
     * @param pageable the pagination and sorting parameters
     * @param nameLike the name to search (accepts SQL like operator)
     * @param olderThan filter by date of birth older than the given date
     * @param youngerThan filter by date of birth younger than the given date
     * @param gender filter by sex ('M' or 'F')
     * @param email search by email address
     * @param phone search by phone number
     * @param addressLike search by address (accepts SQL like operator)
     * @return persons matching the search criteria
     */
    @GetMapping("/persons")
    @ResponseBody
    private Page<Person> getPersons(Pageable pageable,
                                    @RequestParam(value = "name-like", required = false) String nameLike,
                                    @RequestParam(value = "older-than", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate olderThan,
                                    @RequestParam(value = "younger-than", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate youngerThan,
                                    @RequestParam(value = "gender", required = false) Character gender,
                                    @RequestParam(value = "email", required = false) String email,
                                    @RequestParam(value = "phone", required = false) String phone,
                                    @RequestParam(value = "address-like", required = false) String addressLike) {
        Specification<Person> specification = Specification.where(null);
        if (nameLike != null) {
            Objects.requireNonNull(specification);
            specification = specification.and((root, query, builder) -> builder.like(root.get("name"), "%" + nameLike + "%"));
        }
        Objects.requireNonNull(specification);
        if (olderThan != null && youngerThan != null) {
            specification = specification.and((root, query, builder) -> builder.between(root.get("dateOfBirth"), olderThan, youngerThan));
        } else if (olderThan != null) {
            specification = specification.and((root, query, builder) -> builder.greaterThanOrEqualTo(root.get("dateOfBirth"), olderThan));
        } else if (youngerThan != null) {
            specification = specification.and((root, query, builder) -> builder.lessThanOrEqualTo(root.get("dateOfBirth"), youngerThan));
        }
        if (gender != null) {
            Objects.requireNonNull(specification);
            specification = specification.and((root, query, builder) -> builder.equal(root.get("gender"), Character.toUpperCase(gender)));
        }
        if (email != null) {
            Objects.requireNonNull(specification);
            if (email.isEmpty()) {
                specification = specification.and((root, query, builder) -> builder.isNull(root.get("email")));
            } else {
                specification = specification.and((root, query, builder) -> builder.equal(root.get("email"), email.toLowerCase()));
            }
        }
        if (phone != null) {
            Objects.requireNonNull(specification);
            if (phone.isEmpty()) {
                specification = specification.and((root, query, builder) -> builder.isNull(root.get("phone")));
            } else {
                specification = specification.and((root, query, builder) -> builder.equal(root.get("phone"), phone));
            }
        }
        if (addressLike != null) {
            Objects.requireNonNull(specification);
            if (addressLike.isEmpty()) {
                specification = specification.and((root, query, builder) -> builder.isNull(root.get("address")));
            } else {
                specification = specification.and((root, query, builder) -> builder.like(root.get("address"), "%" + addressLike + "%"));
            }
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
