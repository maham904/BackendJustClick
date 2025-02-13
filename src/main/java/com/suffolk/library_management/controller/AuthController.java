package com.suffolk.library_management.controller;

import com.suffolk.library_management.model.AuthRequest;
import com.suffolk.library_management.model.AuthResponse;
import com.suffolk.library_management.model.RegisterRequest;
import com.suffolk.library_management.response.ApiResponse;
import com.suffolk.library_management.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin // For Web
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")  // SignUp
    public ResponseEntity<ApiResponse<RegisterRequest>> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate") // logIn
    public ResponseEntity<ApiResponse<AuthResponse>> authenticate(
            @RequestBody AuthRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @GetMapping("/verify") // logIn
    public ResponseEntity<ApiResponse<AuthResponse>> verify(
            @RequestParam("index") int index,
            @RequestParam("code") String code
    ) {
        return ResponseEntity.ok(service.verify(code.trim(), index));
    }
}
