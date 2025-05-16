package com.project.dto;

import com.project.model.DocumentMetadata;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.InputStreamResource;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FullDocumentDTO {
    private DocumentMetadata metadata;
    private InputStreamResource content;
}
