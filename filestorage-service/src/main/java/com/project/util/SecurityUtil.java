package com.project.util;

import com.project.repository.DocumentMetadataRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component("sec")
public class SecurityUtil {
    public boolean isAuthorByRequest(Jwt jwt, String userId) {
        return jwt != null && jwt.getSubject().equals(userId);
    }

    public boolean isAuthor(String docId, String userId, DocumentMetadataRepository repo) {
        return repo.findById(docId)
                .map(d -> userId.equals(d.getUploaderId()))
                .orElse(false);
    }
}
