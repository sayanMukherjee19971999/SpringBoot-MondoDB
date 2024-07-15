package com.spring.mongodb.controller;

import com.spring.mongodb.entity.Photo;
import com.spring.mongodb.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/photo")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @PostMapping("/add")
    public String addPhoto(@RequestParam("image")MultipartFile image) throws IOException {
        String id=photoService.addPhoto(image.getOriginalFilename(), image);
        return id;
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadPhoto(@PathVariable("id") String id){
        Photo photo=photoService.getPhoto(id);
        Resource resource=new ByteArrayResource(photo.getPhoto().getData());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+photo.getName()+"\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
    }
}
