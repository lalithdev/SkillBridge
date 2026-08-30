package com.skillbridge.opportunity.dto;

import com.skillbridge.opportunity.entity.OpportunityStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Request body for PATCH /opportunities/{id}/status.
 * Per OpenAPI: status can be OPEN or CLOSED (not DRAFT via API).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpportunityStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private OpportunityStatus status;
}
