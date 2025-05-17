package com.project.signatureservice.repository;

import com.project.signatureservice.model.SignerEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignerRepository extends MongoRepository<SignerEntry, String> {
}
