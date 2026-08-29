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
public class AuthResponse {
    @JsonProperty("token")
    private String token;

    @JsonProperty("role")
    private Role role;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("studentProfileId")
    private Long studentProfileId;

    @JsonProperty("companyProfileId")
    private Long companyProfileId;

    @JsonProperty("collegeId")
    private Long collegeId;
}
