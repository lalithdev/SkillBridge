package com.skillbridge.admin.dto;

import com.skillbridge.common.entity.VerificationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVerificationStatusRequest {

    @NotNull(message = "Verification status is required")
    private VerificationStatus status;
}
