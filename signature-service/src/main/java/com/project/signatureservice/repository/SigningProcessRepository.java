package com.project.signatureservice.repository;

import com.project.signatureservice.model.Signature;
import com.project.signatureservice.model.SigningProcess;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SigningProcessRepository extends MongoRepository<SigningProcess, String> {
}
