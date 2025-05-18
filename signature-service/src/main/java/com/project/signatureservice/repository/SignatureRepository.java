package com.project.signatureservice.repository;

import com.project.signatureservice.model.Signature;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SignatureRepository extends MongoRepository<Signature, String> {
    List<Signature> findByDocumentId(String documentId);
    List<Signature> findByAuthorId(String authorId);
}
