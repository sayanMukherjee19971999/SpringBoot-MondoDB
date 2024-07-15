package com.spring.mongodb.reporitory;

import com.spring.mongodb.entity.Person;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface PersonRepository extends MongoRepository<Person, String> {
    List<Person> findByFirstName(String name);

    @Query(value = "{'age' : {$gt:?0, $lt:?1}}", fields = "{address:0}")
    List<Person> getPersonWithinAge(Integer minAge, Integer maxAge);
}
