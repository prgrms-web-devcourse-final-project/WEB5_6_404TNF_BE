package com.grepp.teamnotfound.app.controller.api.auth;

import com.grepp.teamnotfound.app.controller.api.auth.payload.EmailVerificationRequest;
import com.grepp.teamnotfound.app.controller.api.auth.payload.EmailVerifyRequest;
import com.grepp.teamnotfound.app.controller.api.auth.payload.RegisterRequest;
import com.grepp.teamnotfound.app.controller.api.auth.payload.RegisterResponse;
import com.grepp.teamnotfound.app.model.auth.AuthService;
import com.grepp.teamnotfound.app.model.auth.payload.LoginCommand;
import com.grepp.teamnotfound.infra.util.mail.MailService;
import com.grepp.teamnotfound.app.controller.api.auth.payload.LoginRequest;
import com.grepp.teamnotfound.app.controller.api.auth.payload.TokenResponse;
import com.grepp.teamnotfound.app.model.auth.token.dto.TokenDto;
import com.grepp.teamnotfound.app.model.user.UserService;
import com.grepp.teamnotfound.app.model.user.dto.RegisterCommand;
import com.grepp.teamnotfound.infra.auth.token.TokenCookieFactory;
import com.grepp.teamnotfound.infra.auth.token.code.GrantType;
import com.grepp.teamnotfound.infra.auth.token.code.TokenType;
import com.grepp.teamnotfound.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final MailService mailService;

    @Operation(summary = "이메일 중복 확인")
    @GetMapping("v1/check-email")
    public ResponseEntity<ApiResponse<?>> checkEmail(@RequestParam("email") String email) {
        userService.validateEmailDuplication(email);
        return ResponseEntity.ok(ApiResponse.success("사용 가능한 이메일입니다."));
    }

    @Operation(summary = "이메일 인증코드 발송 요청")
    @PostMapping("v1/email-verifications")
    public ResponseEntity<ApiResponse<?>> emailVerification(@RequestBody EmailVerificationRequest request) {
        userService.sendEmail(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("인증 코드가 발송되었습니다."));
    }

    @Operation(summary = "이메일 인증코드 검증")
    @PostMapping("v1/email-verifications/verify")
    public ResponseEntity<ApiResponse<?>> emailVerify(@RequestBody EmailVerifyRequest request) {
        mailService.verifyEmailCode(request.getEmail(), request.getVerificationCode());
        return ResponseEntity.ok(ApiResponse.success("인증코드가 올바르게 인증되었습니다"));
    }

    @Operation(summary = "닉네임 중복 확인")
    @GetMapping("v1/check-nickname")
    public ResponseEntity<ApiResponse<?>> checkNickname(@RequestParam("nickname") String nickname) {
        userService.validateNicknameDuplication(nickname);
        return ResponseEntity.ok(ApiResponse.success("사용 가능한 닉네임입니다."));
    }

    @Operation(summary = "최종 회원가입")
    @PostMapping("v1/register")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody RegisterRequest request) {

        RegisterCommand command = RegisterCommand.builder()
                .email(request.getEmail())
                .name(request.getName())
                .nickname(request.getNickname())
                .password(request.getPassword())
                .build();
        Long userId = userService.registerUser(command);
        RegisterResponse response = new RegisterResponse(userId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }


    @PostMapping("v1/admin/register")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<?>> registerAdmin(@RequestBody RegisterRequest request) {

        RegisterCommand command = RegisterCommand.builder()
                .email(request.getEmail())
                .name(request.getName())
                .nickname(request.getNickname())
                .password(request.getPassword())
                .build();
        Long userId = userService.registerAdmin(command);
        RegisterResponse response = new RegisterResponse(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    @Operation(summary = "회원 로그인")
    @PostMapping("v1/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody LoginRequest request, HttpServletResponse response) {

        LoginCommand command = LoginCommand.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        TokenDto dto = authService.login(command);
        ResponseCookie accessTokenCookie = TokenCookieFactory.create(TokenType.ACCESS_TOKEN.name(),
                dto.getAccessToken(), dto.getAtExpiresIn());
        ResponseCookie refreshTokenCookie = TokenCookieFactory.create(TokenType.REFRESH_TOKEN.name(),
                dto.getRefreshToken(), dto.getRtExpiresIn());

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(dto.getAccessToken())
                .expiresIn(dto.getAtExpiresIn())
                .grantType(GrantType.BEARER)
                .refreshToken(dto.getRefreshToken())
                .build();
        return ResponseEntity.ok(ApiResponse.success(tokenResponse));

    }


    @Operation(summary = "관리자 로그인")
    @PostMapping("v1/admin/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<TokenResponse>> adminLogin(@RequestBody LoginRequest request, HttpServletResponse response) {

        LoginCommand command = LoginCommand.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        TokenDto dto = authService.login(command);
        ResponseCookie accessTokenCookie = TokenCookieFactory.create(TokenType.ACCESS_TOKEN.name(),
                dto.getAccessToken(), dto.getAtExpiresIn());
        ResponseCookie refreshTokenCookie = TokenCookieFactory.create(TokenType.REFRESH_TOKEN.name(),
                dto.getRefreshToken(), dto.getRtExpiresIn());

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
        TokenResponse tokenResponse = TokenResponse.builder()
                .accessToken(dto.getAccessToken())
                .expiresIn(dto.getAtExpiresIn())
                .grantType(GrantType.BEARER)
                .refreshToken(dto.getRefreshToken())
                .build();
        return ResponseEntity.ok(ApiResponse.success(tokenResponse));

    }

}
