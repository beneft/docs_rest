package com.project.templateservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldDto {
    private String name;
    private String type;
    private boolean required;
}