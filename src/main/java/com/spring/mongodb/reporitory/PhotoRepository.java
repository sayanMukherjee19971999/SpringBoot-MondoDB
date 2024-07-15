package com.spring.mongodb.reporitory;

import com.spring.mongodb.entity.Photo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PhotoRepository extends MongoRepository<Photo, String> {
}
