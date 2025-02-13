package com.suffolk.library_management.service;

import com.suffolk.library_management.config.JwtService;
import com.suffolk.library_management.entity.Token;
import com.suffolk.library_management.entity.User;
import com.suffolk.library_management.model.AuthRequest;
import com.suffolk.library_management.model.AuthResponse;
import com.suffolk.library_management.model.RegisterRequest;
import com.suffolk.library_management.model.SendEmailModel;
import com.suffolk.library_management.model.enums.Role;
import com.suffolk.library_management.model.enums.TokenType;
import com.suffolk.library_management.repository.TokenRepository;
import com.suffolk.library_management.repository.UserRepository;
import com.suffolk.library_management.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import static com.suffolk.library_management.utils.Constant.*;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    @Value("${base_url}")
    private String verification_base_url;

    private int status;
    private String message;

    public ApiResponse<RegisterRequest> register(RegisterRequest request) {
        Random rand = new Random();
        String verificationCode = String.format("%04d", rand.nextInt(10000));
        String verificationUrl;
        var user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail().trim())
                .password(request.getPassword())
                .passwordSalt(passwordEncoder.encode(request.getPassword()))
                .createdDate(LocalDateTime.now().toString())
                .modifiedDate(LocalDateTime.now().toString())
                .isActive(true)
                .role(Role.USER)
                .verificationCode(verificationCode)
                .enabled(false)
                .build();

        status = STATUS_CODE_ZERO;
        message = "";
        try {
            repository.save(user);
            verificationUrl = verification_base_url + "auth/verify?index=" + user.getId() + "&code=" + verificationCode;
            status = STATUS_CODE_ONE;
            message = USER_REGISTERED_SUCCESS;
            SendEmailModel emailModel = new SendEmailModel();
            emailModel.setUserEmail(request.getEmail());
            emailModel.setUserName(user.getFullName());
            emailModel.setVerificationLink(verificationUrl);
            emailModel.setSubject(EMAIL_VERIFICATION_SUBJECT);
            emailModel.setLogoData(LOGO_IMAGE_DATA);
            emailModel.setRegisterMessage(EMAIL_VERIFICATION_MESSAGE);
            emailService.sendVerificationEmail(emailModel, true);
        } catch (Exception e) {
            if (e.getCause().getCause().getMessage().contains("Duplicate entry")) {
                status = STATUS_CODE_ZERO;
                message = ACCOUNT_ALREADY_EXISTS;
            } else {
                status = STATUS_CODE_ZERO;
                message = e.getMessage();
            }
        }
        return ApiResponse.<RegisterRequest>builder().status(status).message(message).data(null).build();
    }

    public ApiResponse<AuthResponse> authenticate(AuthRequest request) {
        Optional<User> userData;
        var jwtToken = "";
        status = STATUS_CODE_ZERO;
        message = "";
        AuthResponse response = new AuthResponse();
        try {
            if (checkRequestEmailAndPassword(request.getEmail(), request.getPassword())) {
                userData = repository.findByEmail(request.getEmail().trim());
                System.out.println("User: " + userData);
                if (userData.isPresent()) {
                    var user = userData.get();
                    if (user.getPassword().equals(request.getPassword())) {
                        if (user.isEnabled()) {
                            jwtToken = jwtService.generateToken(user);
                            saveUserToken(user, jwtToken);
                            response = new AuthResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole().name(), "", jwtToken);
                            status = STATUS_CODE_ONE;
                            message = SUCCESS;
                        } else {
                            status = STATUS_CODE_ZERO;
                            message = NON_VERIFIED_USER;
                        }
                    } else {
                        status = STATUS_CODE_ZERO;
                        message = INVALID_PASSWORD;
                    }

                } else {
                    status = STATUS_CODE_ZERO;
                    message = INVALID_EMAIL;
                }
            } else {
                status = STATUS_CODE_ZERO;
                message = NULL_REQUEST;
            }
        } catch (Exception e) {
            message = "Exception : " + e.getMessage();
        }
        return ApiResponse.<AuthResponse>builder()
                .status(status)
                .message(message)
                .data(response)
                .build();
    }

    public ApiResponse<AuthResponse> verify(String code, int index) {
        System.out.println("Verification Code : " + code);
        System.out.println("User Index : " + index);
        status = STATUS_CODE_ZERO;
        message = "";
        try {
            var user = repository.findByUserIdAndVerificationCode(code, index);
            System.out.println("User : " + user);
            if (user == null) {
                message = "Failed to verify user";
            } else {
                user.setVerificationCode(null);
                user.setEnabled(true);
                repository.save(user);
                status = 1;
                message = "User Verified Successfully";
            }
        } catch (Exception e) {
            message = "Exception : " + e.getMessage();
        }
        return ApiResponse.<AuthResponse>builder()
                .status(status)
                .message(message)
                .build();
    }


    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private Boolean checkRequestEmailAndPassword(String email, String password) {
        return (!email.isEmpty() || !email.isBlank()) && (!password.isEmpty() || !password.isBlank());
    }
}
