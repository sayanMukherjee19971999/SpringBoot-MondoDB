package com.spring.mongodb.service.implementation;

import com.spring.mongodb.entity.Person;
import com.spring.mongodb.reporitory.PersonRepository;
import com.spring.mongodb.service.PersonService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonRepository personRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Person save(Person person) {
        return personRepo.save(person);
    }

    @Override
    public List<Person> getPersonByFirstName(String name) {
        return personRepo.findByFirstName(name);
    }

    @Override
    public void deletePersonById(String id) {
        personRepo.deleteById(id);
    }

    @Override
    public List<Person> getPersonWithinAge(Integer minAge, Integer maxAge) {
        return personRepo.getPersonWithinAge(minAge, maxAge);
    }

    @Override
    public Page<Person> search(String name, Integer minAge, Integer maxAge, String city, Pageable pageable) {
        Query query=new Query().with(pageable);
        List<Criteria> criteria=new ArrayList<>();
        if(name!=null && !name.isEmpty()){
            criteria.add(Criteria.where("firstName").regex(name, "i"));
        }
        if(minAge!=null && maxAge!=null){
            criteria.add(Criteria.where("age").gte(minAge).lte(maxAge));
        }
        if(city!=null && !city.isEmpty()){
            criteria.add(Criteria.where("address.city").is(city));
        }
        if(!criteria.isEmpty()){
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }
        Page<Person> people= PageableExecutionUtils.getPage(
                mongoTemplate.find(query, Person.class), pageable, ()->mongoTemplate.count(query.skip(0).limit(0), Person.class)
        );
        return people;
    }

    @Override
    public List<Document> getOldestPersonByCity() {
        UnwindOperation unwindOperation1 = Aggregation.unwind("address");
        GroupOperation groupByCityAndMaxAgeOperation = Aggregation.group("address.city").max("age").as("maxAge");
        Aggregation aggregationStep1 = Aggregation.newAggregation(
                unwindOperation1,
                groupByCityAndMaxAgeOperation
        );
        List<Document> maxAgeResults = mongoTemplate.aggregate(aggregationStep1, Person.class, Document.class).getMappedResults();
        List<Criteria> criteriaList = new ArrayList<>();
        for (Document result : maxAgeResults) {
            String city = result.getString("_id");
            Integer maxAge = result.getInteger("maxAge");
            criteriaList.add(Criteria.where("address.city").is(city).and("age").is(maxAge));
        }
        Criteria criteria = new Criteria().orOperator(criteriaList.toArray(new Criteria[0]));
        MatchOperation matchOperation2 = Aggregation.match(criteria);
        Aggregation aggregationStep2 = Aggregation.newAggregation(
                unwindOperation1,
                matchOperation2
        );
        List<Document> persons = mongoTemplate.aggregate(aggregationStep2, Person.class, Document.class).getMappedResults();
        return persons;

    }

    @Override
    public List<Document> getPopulationByCity() {
        UnwindOperation unwindOperation=Aggregation.unwind("address");
        GroupOperation groupOperation=Aggregation.group("address.city").count().as("populationCount");
        SortOperation sortOperation=Aggregation.sort(Sort.Direction.DESC, "populationCount");
        ProjectionOperation projectionOperation=Aggregation.project().andExpression("_id").as("city").andExpression("populationCount").as("count").andExclude("_id");
        Aggregation aggregation=Aggregation.newAggregation(unwindOperation, groupOperation, sortOperation, projectionOperation);
        List<Document> documents=mongoTemplate.aggregate(aggregation, Person.class, Document.class).getMappedResults();
        return documents;
    }
}
