package com.bwgjoseph.springbootdebeziummongodbes.mongo;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/persons")
public class PersonController {
    private final PersonRepository personRepository;

    @PostMapping
    public Person create(@RequestBody PersonRequest personRequest) {
        Person person = Person.builder()
                    .name(personRequest.name())
                    .description(personRequest.description())
                    .hashTags(personRequest.hashTags())
                    .build();

        return this.personRepository.save(person);
    }

    @PutMapping("/{id}")
    public Person update(@PathVariable String id, @RequestBody PersonRequest personRequest) {
        Person currentPerson = this.personRepository.findById(id).orElseThrow();

        Person updatedPerson = currentPerson.toBuilder()
                            .name(personRequest.name())
                            .description(personRequest.description())
                            .hashTags(personRequest.hashTags())
                            .build();

        return this.personRepository.save(updatedPerson);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        this.personRepository.deleteById(id);
    }
}
