package com.skillbridge.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbridge.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserResponse {
    private Long userId;
    private String email;
    private Role role;

    @JsonProperty("isActive")
    private boolean active;

    private Long studentProfileId;
    private Long companyProfileId;
    private Long collegeId;
}
