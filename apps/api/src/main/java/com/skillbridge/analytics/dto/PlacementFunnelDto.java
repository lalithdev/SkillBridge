package com.skillbridge.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlacementFunnelDto {

    private Integer applied;
    private Integer underReview;
    private Integer shortlisted;
    private Integer interview;
    private Integer selected;
    private Integer rejected;
    private Integer totalApplications;
}
