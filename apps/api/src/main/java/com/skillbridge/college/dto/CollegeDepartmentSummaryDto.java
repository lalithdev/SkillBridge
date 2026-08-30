package com.skillbridge.college.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollegeDepartmentSummaryDto {

    private Long departmentId;
    private String departmentName;
    private String departmentCode;
    private Integer studentCount;
}
