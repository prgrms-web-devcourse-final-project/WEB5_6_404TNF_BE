package com.grepp.teamnotfound.infra.auth.oauth2;

import com.grepp.teamnotfound.app.model.auth.AuthService;
import com.grepp.teamnotfound.app.model.auth.oauth.dto.CustomOAuth2UserDto;
import com.grepp.teamnotfound.app.model.auth.token.dto.TokenDto;
import com.grepp.teamnotfound.infra.auth.token.TokenCookieFactory;
import com.grepp.teamnotfound.infra.auth.token.code.TokenType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2UserDto customOAuth2User = (CustomOAuth2UserDto) authentication.getPrincipal();

        Long userId = customOAuth2User.getUserId();

        TokenDto dto = authService.processTokenLogin(userId);

        ResponseCookie accessTokenCookie = TokenCookieFactory.create(TokenType.ACCESS_TOKEN.name(),
                dto.getAccessToken(), dto.getAtExpiresIn());
        ResponseCookie refreshTokenCookie = TokenCookieFactory.create(TokenType.REFRESH_TOKEN.name(),
                dto.getRefreshToken(), dto.getRtExpiresIn());

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        log.info("3️⃣ User 회원 로그인: {}", customOAuth2User.getUsername());

        // TODO 회원 로그인 후 메인화면(실재 화면 경로)
        getRedirectStrategy().sendRedirect(request, response, "/user/login");
    }
}
