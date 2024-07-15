package com.spring.mongodb.service.implementation;

import com.spring.mongodb.entity.Photo;
import com.spring.mongodb.reporitory.PhotoRepository;
import com.spring.mongodb.service.PhotoService;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PhotoServiceImplementation implements PhotoService {

    @Autowired
    private PhotoRepository photoRepo;

    @Override
    public String addPhoto(String originalFilename, MultipartFile image) throws IOException {
        Photo photo=new Photo();
        photo.setName(originalFilename);
        photo.setPhoto(new Binary(BsonBinarySubType.BINARY,image.getBytes()));
        return photoRepo.save(photo).getId();
    }

    @Override
    public Photo getPhoto(String id) {
        return photoRepo.findById(id).get();
    }
}
