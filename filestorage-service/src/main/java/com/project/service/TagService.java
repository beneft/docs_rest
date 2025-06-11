package com.project.service;

import com.project.model.DocumentMetadata;
import com.project.model.TagList;
import com.project.repository.DocumentMetadataRepository;
import com.project.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final DocumentMetadataRepository metadataRepository;

    public List<String> getTags(String userId) {
        return tagRepository.findByUserId(userId)
                .map(TagList::getTags)
                .orElse(Collections.emptyList());
    }

    public void addTag(String userId, String tag) {
        TagList tagList = tagRepository.findByUserId(userId)
                .orElse(TagList.builder().userId(userId).build());

        if (!tagList.getTags().contains(tag)) {
            tagList.getTags().add(tag);
            tagRepository.save(tagList);
        }
    }

    public void removeTag(String userId, String tag) {
        tagRepository.findByUserId(userId).ifPresent(tagList -> {
            if (tagList.getTags().remove(tag)) {
                tagRepository.save(tagList);
            }
        });
        List<DocumentMetadata> metadataList = metadataRepository.findByUploaderId(userId);
        for (DocumentMetadata metadata : metadataList) {
            if (metadata.getTags() != null && metadata.getTags().remove(tag)) {
                metadataRepository.save(metadata);
            }
        }
    }
}
