package com.hotel.system.controller;

import com.hotel.system.dto.DTOs;
import com.hotel.system.service.impl.AuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthServiceImpl authService;

    @PostMapping("/register")
    public ResponseEntity<DTOs.AuthResponse> register(@RequestBody DTOs.RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<DTOs.AuthResponse> login(@RequestBody DTOs.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<Map<String, Object>> verifyEmail(@RequestParam String token) {
        boolean verified = authService.verifyEmail(token);
        Map<String, Object> response = new HashMap<>();
        response.put("success", verified);
        response.put("message", "Email verified successfully! You can now login.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Map<String, Object>> resendVerification(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        authService.resendVerificationEmail(email);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Verification email sent successfully!");
        return ResponseEntity.ok(response);
    }
}
