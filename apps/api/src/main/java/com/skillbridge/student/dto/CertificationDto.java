package com.skillbridge.student.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificationDto {

    private Long id;
    private String title;
    private String issuer;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate issuedDate;

    private String certificateUrl;
}
