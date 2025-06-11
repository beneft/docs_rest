package com.project.repository;

import com.project.model.RecievedDocuments;
import com.project.model.TagList;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TagRepository extends MongoRepository<TagList, String> {
    Optional<TagList> findByUserId(String userId);
}
