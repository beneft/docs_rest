package com.example.commondto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NcaVerifyRequest {
    private List<String> revocationCheck = List.of("OCSP");
    private String cms;
    private String data;
}
