package com.project.repository;

import com.project.model.RecievedDocuments;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface RecievedDocumentsRepository extends MongoRepository<RecievedDocuments, String> {
    Optional<RecievedDocuments> findByUserId(String userId);
}
