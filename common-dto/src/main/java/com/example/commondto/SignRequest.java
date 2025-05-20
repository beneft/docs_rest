package com.example.commondto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class SignRequest {
    private Long userId;
    private String documentId;
}
