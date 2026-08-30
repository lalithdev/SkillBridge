package com.skillbridge.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbridge.user.entity.Role;
import com.skillbridge.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDto {

    private Long id;
    private String email;
    private Role role;

    @JsonProperty("isActive")
    private boolean isActive;

    private Instant createdAt;

    public static AdminUserDto from(User user) {
        if (user == null) {
            return null;
        }
        return AdminUserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
