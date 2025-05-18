package com.project.templateservice.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateDto {
    private String name;
    private List<FieldDto> fields;
}
