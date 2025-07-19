package com.grepp.teamnotfound.infra.auth.oauth2;

import com.grepp.teamnotfound.app.model.auth.AuthService;
import com.grepp.teamnotfound.app.model.auth.token.RefreshTokenService;
import com.grepp.teamnotfound.infra.auth.token.JwtProvider;
import com.grepp.teamnotfound.infra.auth.token.TokenCookieFactory;
import com.grepp.teamnotfound.infra.auth.token.code.TokenType;
import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        log.error("3️⃣ 로그인 실패 : {}", exception.getMessage(), exception);

        String errorMessage;
        if (exception.getMessage().contains("다른 provider로 가입된 이메일")) {
            errorMessage = "이미 다른 계정으로 가입된 이메일입니다.";
        } else if (exception.getMessage().contains("지원하지 않는 OAuth2 provider")) {
            errorMessage = "지원하지 않는 소셜 로그인입니다.";
        }
        else {
            errorMessage = "소셜 로그인에 실패했습니다.";
        }

        // TODO error 페이지(실재 화면 경로)
        String redirectUrl = "/error/login?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
