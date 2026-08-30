package com.skillbridge.admin.dto;

import com.skillbridge.common.entity.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationItemDto {

    private Long id;
    private String type;
    private String name;
    private String contactEmail;
    private String website;
    private VerificationStatus verificationStatus;
    private Instant submittedAt;
}
