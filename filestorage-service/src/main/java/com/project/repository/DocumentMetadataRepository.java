package com.project.repository;

import com.project.model.DocumentMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DocumentMetadataRepository extends MongoRepository<DocumentMetadata, String> {
    List<DocumentMetadata> findByUploaderId(String uploaderId);
    List<DocumentMetadata> findByTagsIn(List<String> tags);
}
