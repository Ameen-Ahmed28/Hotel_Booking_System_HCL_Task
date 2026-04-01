package com.hotel.system.service.impl;

import com.hotel.system.dto.DTOs;
import com.hotel.system.entity.User;
import com.hotel.system.exception.BadRequestException;
import com.hotel.system.exception.EmailNotVerifiedException;
import com.hotel.system.exception.ResourceNotFoundException;
import com.hotel.system.repository.UserRepository;
import com.hotel.system.security.JwtUtil;
import com.hotel.system.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class AuthServiceImpl {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private EmailService emailService;
    
    @Value("${app.skip-email-verification:false}")
    private boolean skipEmailVerification;

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    public DTOs.AuthResponse register(DTOs.RegisterRequest request) {
        // Validate email format
        if (!isValidEmail(request.getEmail())) {
            throw new BadRequestException("Invalid email format. Please provide a valid email address.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists. Please use a different email or login.");
        }

        // Validate password strength
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters long.");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.valueOf(request.getRole()));
        user.setEnabled(true);
        
        // Skip email verification in development mode
        if (skipEmailVerification) {
            user.setEmailVerified(true);
            user.setVerificationToken(null);
            user.setVerificationTokenExpiry(null);
        } else {
            user.setEmailVerified(false);
            // Generate verification token
            String verificationToken = UUID.randomUUID().toString();
            user.setVerificationToken(verificationToken);
            user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
        }
        
        userRepository.save(user);

        // Send verification email only if not skipping
        if (!skipEmailVerification) {
            emailService.sendVerificationEmail(user.getEmail(), user.getVerificationToken(), user.getName());
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        return new DTOs.AuthResponse(token, user, false);
    }

    public DTOs.AuthResponse login(DTOs.LoginRequest request) {
        // Validate email format
        if (!isValidEmail(request.getEmail())) {
            throw new BadRequestException("Invalid email format.");
        }

        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        // Check if email is verified (skip in development mode)
        if (!skipEmailVerification && !user.isEmailVerified()) {
            // Resend verification email
            String verificationToken = UUID.randomUUID().toString();
            user.setVerificationToken(verificationToken);
            user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
            userRepository.save(user);
            emailService.sendVerificationEmail(user.getEmail(), verificationToken, user.getName());
            
            throw new EmailNotVerifiedException("Please verify your email address first. A new verification email has been sent to " + user.getEmail());
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().toLowerCase(), request.getPassword())
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail().toLowerCase());
        String token = jwtUtil.generateToken(userDetails);
        return new DTOs.AuthResponse(token, user, true);
    }

    public boolean verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid verification token."));

        if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Verification token has expired. Please request a new one.");
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
        
        return true;
    }

    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (user.isEmailVerified()) {
            throw new BadRequestException("Email is already verified.");
        }

        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), verificationToken, user.getName());
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
