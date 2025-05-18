package com.project.templateservice.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Field {
    private String name;
    private String type; // text, number, date
    private boolean required;
}
