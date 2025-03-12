package com.project.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.project.dto.SignRequest;
import com.project.dto.SigningStatus;
import com.project.model.Signer;
import com.project.model.SignerEntry;
import com.project.repository.SignerRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class FileStorageService {
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations gridFsOperations;

    @Autowired
    private SignerRepository signerRepository;

    public String storeFile(MultipartFile file) throws IOException {
        ObjectId id = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
        return id.toHexString();
    }

    public GridFSFile getFile(String id) {
        return gridFsTemplate.findOne(query(where("_id").is(id)));
    }

    public List<Map<String, Object>> listFiles() {
        return StreamSupport.stream(gridFsTemplate.find(new Query()).spliterator(), false)
                .map(file -> {
                    Map<String, Object> fileInfo = new HashMap<>();
                    fileInfo.put("_id", file.getObjectId().toString());
                    fileInfo.put("filename", file.getFilename());
                    fileInfo.put("uploadDate", file.getUploadDate());
                    return fileInfo;
                })
                .collect(Collectors.toList());
    }

    public void deleteFile(String id) {
        gridFsTemplate.delete(query(where("_id").is(id)));
    }

    public void addSigner(SignRequest request) {
        SignerEntry entry = signerRepository.findById(request.getDocumentId())
                .orElse(new SignerEntry(request.getDocumentId(), new ArrayList<>()));

        boolean alreadyExists = entry.getSigners().stream()
                .anyMatch(signer -> signer.getUserId().equals(request.getUserId()));

        if (!alreadyExists) {
            entry.getSigners().add(new Signer(request.getUserId(), SigningStatus.PENDING));
            signerRepository.save(entry);
        }
    }

    public String signDocument(SignRequest request) {
        Optional<SignerEntry> entryOpt = signerRepository.findById(request.getDocumentId());
        if (entryOpt.isEmpty()) {
            return "Document not found";
        }

        SignerEntry entry = entryOpt.get();
        for (Signer signer : entry.getSigners()) {
            if (signer.getUserId().equals(request.getUserId()) && signer.getStatus().equals(SigningStatus.PENDING)) {
                signer.setStatus(SigningStatus.SIGNED);
                signerRepository.save(entry);
                return "Document signed";
            }
        }
        return "User not assigned as signer or already signed";
    }

    public List<Signer> getSigners(String documentId) {
        return signerRepository.findById(documentId)
                .map(SignerEntry::getSigners)
                .orElse(Collections.emptyList());
    }
}
