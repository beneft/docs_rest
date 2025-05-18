package com.project.templateservice.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentItem {
    private String id;
    private String name;
    private String contentType;
}
