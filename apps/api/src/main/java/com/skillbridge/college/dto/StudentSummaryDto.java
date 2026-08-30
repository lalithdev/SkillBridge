package com.skillbridge.college.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentSummaryDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String departmentName;
    private Short yearOfStudy;
    private BigDecimal cgpa;
    private Integer skillCount;
}
