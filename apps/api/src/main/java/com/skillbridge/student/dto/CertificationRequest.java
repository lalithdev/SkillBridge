package com.skillbridge.student.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificationRequest {

    @NotBlank(message = "Certification title is required")
    @Size(max = 255, message = "Certification title must not exceed 255 characters")
    private String title;

    @Size(max = 255, message = "Issuer must not exceed 255 characters")
    private String issuer;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate issuedDate;

    @Size(max = 500, message = "Certificate URL must not exceed 500 characters")
    private String certificateUrl;
}
