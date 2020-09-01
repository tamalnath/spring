package org.tamal.spring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Get a list of persons")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "the list of persons", content = {
                  @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
            })
    })
    @GetMapping("/persons")
    @ResponseBody
    private Page<Person> getPersons(Pageable pageable,
                                    @RequestParam(value = "name-like", required = false) @Parameter(description = "Name (like)") String nameLike,
                                    @RequestParam(value = "older-than", required = false) @Parameter(description = "Date of Birth older than") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate olderThan,
                                    @RequestParam(value = "younger-than", required = false) @Parameter(description = "Date of Birth younger than") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate youngerThan,
                                    @RequestParam(value = "gender", required = false) @Parameter(description = "Gender/sex") Character gender,
                                    @RequestParam(value = "email", required = false) @Parameter(description = "Email address") String email,
                                    @RequestParam(value = "phone", required = false) @Parameter(description = "Phone/mobile no.") String phone,
                                    @RequestParam(value = "address-like", required = false) @Parameter(description = "Address (like)") String addressLike) {
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

    @Operation(summary = "Get a person by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "the person", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Person.class))
            }),
            @ApiResponse(responseCode = "404", description = "None found")
    })
    @GetMapping("/persons/{id}")
    @ResponseBody
    private Person getPerson(@PathVariable("id") @Parameter(description = "Person ID") int id) {
        return personService.getPerson(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/persons/{id}")
    private void deletePerson(@PathVariable("id") int id) {
        personService.delete(id);
    }

    @Operation(summary = "Save or update a person")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The person ID", content = {
                    @Content(mediaType = "text/plain", schema = @Schema(implementation = Integer.class))
            })
    })
    @PostMapping("/persons")
    @ResponseBody
    private int savePerson(@RequestBody @Parameter(description = "The Person") Person person) {
        personService.saveOrUpdate(person);
        return person.getId();
    }

}
