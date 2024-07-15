package com.spring.mongodb.service;

import com.spring.mongodb.entity.Person;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PersonService {

    Person save(Person person);

    List<Person> getPersonByFirstName(String name);

    void deletePersonById(String id);

    List<Person> getPersonWithinAge(Integer minAge, Integer maxAge);

    Page<Person> search(String name, Integer minAge, Integer maxAge, String city, Pageable pageable);

    List<Document> getOldestPersonByCity();

    List<Document> getPopulationByCity();
}
