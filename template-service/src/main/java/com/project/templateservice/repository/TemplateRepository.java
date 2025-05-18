package com.project.templateservice.repository;

import com.project.templateservice.model.Template;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TemplateRepository extends MongoRepository<Template, String> {
    Optional<Template> findByFileId(String fileId);
}
