package com.suffolk.library_management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private Integer id;
    private String fullName;
    private String email;
    private String role;
    private String password;
    private String accessToken;
}
