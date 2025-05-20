package com.example.commondto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CmsDetailsDTO {
    private boolean cmsValid;
    private List<SignatureDTO> signatures;
    private String uploaderId;
    private LocalDateTime createdDate;

}
