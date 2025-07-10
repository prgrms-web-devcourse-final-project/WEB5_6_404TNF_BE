package com.grepp.teamnotfound.infra.auth.token.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grepp.teamnotfound.app.model.auth.AuthService;
import com.grepp.teamnotfound.infra.auth.token.JwtProvider;
import com.grepp.teamnotfound.infra.auth.token.TokenCookieFactory;
import com.grepp.teamnotfound.infra.auth.token.code.TokenType;
import com.grepp.teamnotfound.infra.error.exception.AuthException;
import com.grepp.teamnotfound.infra.error.exception.code.AuthErrorCode;
import com.grepp.teamnotfound.infra.response.ApiResponse;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogoutFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        if (!path.equals("/api/v1/auth/logout")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String accessToken = jwtProvider.resolveToken(request, TokenType.ACCESS_TOKEN);

            if (accessToken != null) {
                // 1. 로그아웃 수행
                authService.logout(accessToken);
                response.setStatus(HttpStatus.OK.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                objectMapper.writeValue(response.getWriter(), ApiResponse.success("로그아웃에 성공했습니다."));
            } else {
                response.setStatus(HttpStatus.OK.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                objectMapper.writeValue(response.getWriter(), ApiResponse.success("이미 로그아웃된 상태입니다."));
            }

        } catch (AuthException e) {
            sendLogoutErrorResponse(response, AuthErrorCode.ALREADY_LOGGED_OUT, AuthErrorCode.ALREADY_LOGGED_OUT.getMessage());
        } catch (JwtException e) {
            sendLogoutErrorResponse(response, AuthErrorCode.INVALID_TOKEN, AuthErrorCode.INVALID_TOKEN.getMessage());
        } catch (Exception e) {
            sendLogoutErrorResponse(response, AuthErrorCode.UNAUTHENTICATED, AuthErrorCode.UNAUTHORIZED.getMessage());
        } finally {
            expiatedCookies(response);
        }
    }

    private void expiatedCookies(HttpServletResponse response) {
        ResponseCookie expiredAccessToken = TokenCookieFactory.createExpiredToken(TokenType.ACCESS_TOKEN);
        ResponseCookie expiredRefreshToken = TokenCookieFactory.createExpiredToken(TokenType.REFRESH_TOKEN);
        ResponseCookie expiredSessionId = TokenCookieFactory.createExpiredToken(TokenType.AUTH_SERVER_SESSION_ID);

        response.addHeader("Set-Cookie", expiredAccessToken.toString());
        response.addHeader("Set-Cookie", expiredRefreshToken.toString());
        response.addHeader("Set-Cookie", expiredSessionId.toString());
    }

    private void sendLogoutErrorResponse(HttpServletResponse response, AuthErrorCode errorCode, String message) throws IOException {
        response.setStatus(errorCode.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), ApiResponse.error(errorCode.getCode(), message));
    }

    // swagger 프리플라이트 방지
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }
}
