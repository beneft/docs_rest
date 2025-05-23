package com.project.service;
import com.example.commondto.DocumentMetadataDTO;
import com.example.commondto.DocumentStatus;
import com.example.commondto.DocumentType;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.project.model.DocumentMetadata;
import com.project.repository.DocumentMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
@RequiredArgsConstructor
public class DocumentService {
    private final GridFsTemplate gridFsTemplate;
    private final GridFsOperations gridFsOperations;
    private final DocumentMetadataRepository metadataRepository;
    private final MongoTemplate mongoTemplate;

    public String uploadDocument(MultipartFile file, DocumentMetadata metadata) throws IOException {
        ObjectId fileId = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
        metadata.setId(fileId.toHexString());
        metadata.setUploadDate(LocalDateTime.now());
        metadata.setContentType(file.getContentType());
        metadata.setStatus(DocumentStatus.ACTIVE);
        metadataRepository.save(metadata);
        return fileId.toHexString();
    }

    public String uploadDocumentRaw(byte[] data, DocumentMetadata metadata) {
        ObjectId fileId = gridFsTemplate.store(new ByteArrayInputStream(data), metadata.getName(), metadata.getContentType());
        metadata.setId(fileId.toHexString());
        metadataRepository.save(metadata);
        return fileId.toHexString();
    }

    public Optional<GridFsResource> downloadDocument(String id) {
        GridFSFile file = gridFsTemplate.findOne(query(where("_id").is(id)));
        return file != null ? Optional.of(gridFsOperations.getResource(file)) : Optional.empty();
    }

    public void deleteDocument(String id) {
        gridFsTemplate.delete(query(where("_id").is(id)));
        metadataRepository.deleteById(id);
    }

    public List<DocumentMetadata> listAllMetadata() {
        return metadataRepository.findAll();
    }

    public DocumentMetadata getMetadata(String id) {
        return metadataRepository.findById(id).get();
    }

    public List<DocumentMetadataDTO> searchDocumentsMetadata(String uploaderId, String documentId, String name,
                                                             List<String> tags, String fromDate, String toDate, DocumentType type) {
        Query query = new Query();
        Criteria criteria = new Criteria();

        if (uploaderId != null) criteria.and("uploaderId").is(uploaderId);
        if (documentId != null) criteria.and("_id").is(documentId);
        if (name != null) criteria.and("name").regex(".*" + name + ".*", "i");
        if (tags != null && !tags.isEmpty()) criteria.and("tags").in(tags);
        if (type != null) criteria.and("type").is(type);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        if (fromDate != null || toDate != null) {
            Criteria dateCriteria = Criteria.where("uploadDate");
            if (fromDate != null) dateCriteria.gte(LocalDateTime.parse(fromDate, formatter));
            if (toDate != null) dateCriteria.lte(LocalDateTime.parse(toDate, formatter));
            criteria.andOperator(dateCriteria);
        }

        query.addCriteria(criteria);

        List<DocumentMetadata> results = mongoTemplate.find(query, DocumentMetadata.class);
        return results.stream()
                .map(this::MetadataToDto)
                .collect(Collectors.toList());
    }



    public DocumentMetadataDTO updateMetadata(String id, DocumentMetadataDTO updatedMetadata) {
        DocumentMetadata existing = metadataRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document metadata not found with ID: " + id));

        existing.setName(updatedMetadata.getName());
        existing.setDescription(updatedMetadata.getDescription());
        existing.setTags(updatedMetadata.getTags());
        existing.setUploaderId(updatedMetadata.getUploaderId());
        existing.setExpirationDate(updatedMetadata.getExpirationDate());
        existing.setType(updatedMetadata.getType());

        metadataRepository.save(existing);
        return MetadataToDto(existing);
    }

    public DocumentMetadataDTO makeSent(String id) {
        DocumentMetadata existing = metadataRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document metadata not found with ID: " + id));

        existing.setType(DocumentType.SENT);

        metadataRepository.save(existing);
        return MetadataToDto(existing);
    }


    @Scheduled(cron = "0 0 * * * *")
    public void expireDocuments() {
        List<DocumentMetadata> all = metadataRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        for (DocumentMetadata metadata : all) {
            if (metadata.getExpirationDate() != null &&
                    metadata.getExpirationDate().isBefore(now) &&
                    metadata.getStatus() != DocumentStatus.EXPIRED) {
                metadata.setStatus(DocumentStatus.EXPIRED);
                metadataRepository.save(metadata);
            }
        }
    }

    private DocumentMetadataDTO MetadataToDto(DocumentMetadata entity) {
        return DocumentMetadataDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .contentType(entity.getContentType())
                .uploadDate(entity.getUploadDate())
                .expirationDate(entity.getExpirationDate())
                .uploaderId(entity.getUploaderId())
                .tags(entity.getTags())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .type(entity.getType())
                .build();
    }

    public List<DocumentMetadataDTO> findByUploaderId(String uploaderId) {
        List<DocumentMetadata> list = metadataRepository.findAll().stream()
                .filter(doc -> uploaderId.equals(doc.getUploaderId()))
                .collect(Collectors.toList());

        return list.stream()
                .map(this::MetadataToDto)
                .collect(Collectors.toList());
    }

}
