package com.grepp.teamnotfound.infra.auth.token.filter;

import com.grepp.teamnotfound.app.model.auth.token.RefreshTokenService;
import com.grepp.teamnotfound.app.model.auth.token.dto.AccessTokenDto;
import com.grepp.teamnotfound.app.model.auth.token.entity.RefreshToken;
import com.grepp.teamnotfound.app.model.auth.token.entity.UserBlackList;
import com.grepp.teamnotfound.app.model.auth.token.repository.UserBlackListRepository;
import com.grepp.teamnotfound.infra.auth.token.JwtProvider;
import com.grepp.teamnotfound.infra.auth.token.TokenCookieFactory;
import com.grepp.teamnotfound.infra.auth.token.code.TokenType;
import com.grepp.teamnotfound.infra.error.exception.CommonException;
import com.grepp.teamnotfound.infra.error.exception.code.AuthErrorCode;
import com.grepp.teamnotfound.infra.util.requestmatcher.RequestMatcherHolder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserBlackListRepository userBlackListRepository;

    private final RequestMatcherHolder requestMatcherHolder;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        // OPTIONS(프리플라이트) 요청은 필터를 타지 않도록 처리
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        return requestMatcherHolder.getRequestMatchersByMinRole(null).matches(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info(request.getRequestURI());

        // 1. accessToken 확인 - 토큰이 없는 경우 filtering
        String accessToken = jwtProvider.resolveToken(request, TokenType.ACCESS_TOKEN);
        if (accessToken == null) {
            throw new CommonException(AuthErrorCode.UNAUTHENTICATED);
        }

        // 2. parseClaim - // 블랙리스트 filtering
        Claims claims = jwtProvider.parseClaims(accessToken);
        if(userBlackListRepository.existsById(claims.getSubject())){
            throw new CommonException(AuthErrorCode.INVALID_TOKEN);
        }

        // 3. 유효성 검증 validateToken
        try {
            if(jwtProvider.validateToken(accessToken)) {
                Authentication authentication = jwtProvider.generateAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            // 3-1. 만료된 토큰 -> 재발급 - 재발급 실패 시 filtering
        } catch (ExpiredJwtException e) {
            AccessTokenDto newAccessToken = renewingAccessToken(accessToken, request);
            if(newAccessToken == null) {
                filterChain.doFilter(request, response);
                return;
            }
            // 3-2. refreshToken 갱신
            RefreshToken newRefreshToken = renewingRefreshToken(claims.getId(), newAccessToken.getId());
            // 갱신된 Token으로 응답 생성
            responseToken(response, newAccessToken, newRefreshToken);
        }

        filterChain.doFilter(request, response);
    }



    // AccessToken 만료 시 재발급
    private AccessTokenDto renewingAccessToken(String accessToken, HttpServletRequest request) {
        // 만료된 accessToken로 인증 객체 생성
        Authentication authentication = jwtProvider.generateAuthentication(accessToken);
        String refreshToken = jwtProvider.resolveToken(request, TokenType.REFRESH_TOKEN);
        // 인증 객체에서 정보 추출
        Claims claims = jwtProvider.parseClaims(accessToken);

        // 추출된 정보의 id 값으로 refresh 토큰 접근
        RefreshToken storedRefreshToken = refreshTokenService.findByAccessTokenId(claims.getId());
        if(storedRefreshToken == null) {
            throw new CommonException(AuthErrorCode.INVALID_TOKEN);
        }

        // 같은 값을 지닌 refresh 없을 때 (보안 이슈)
        if(!storedRefreshToken.getToken().equals(refreshToken)){
            long remainingExpirationSeconds = (claims.getExpiration().getTime() - System.currentTimeMillis()) / 1000;

            // 유효 시간이 있을 때만 blacklist 처리
            if (remainingExpirationSeconds > 0) {
                userBlackListRepository.save(new UserBlackList(authentication.getName(), remainingExpirationSeconds));
                throw new CommonException(AuthErrorCode.SECURITY_INCIDENT);
            } else {
                // access 만료 + re 만료 : 아예 자격이 없음
                throw new CommonException(AuthErrorCode.INVALID_TOKEN);
            }
        }

        return generateAccessToken(authentication);
    }

    private AccessTokenDto generateAccessToken(Authentication authentication) {
        AccessTokenDto newAccessToken = jwtProvider.generateAccessToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return newAccessToken;
    }

    // accessToken 재발급 시, refreshToken 재발급 로직
    private RefreshToken renewingRefreshToken(String id, String newTokenId) {
        return refreshTokenService.renewingToken(id, newTokenId);
    }

    // 응답에 쿠키로 토큰 전달
    private void responseToken(HttpServletResponse response, AccessTokenDto newAccessToken, RefreshToken newRefreshToken) {
        ResponseCookie accessTokenCookie =
                TokenCookieFactory.create(TokenType.ACCESS_TOKEN.name(), newAccessToken.getToken(),
                        jwtProvider.getAtExpiration()
                );

        ResponseCookie refreshTokenCookie =
                TokenCookieFactory.create(TokenType.REFRESH_TOKEN.name(), newRefreshToken.getToken(),
                        jwtProvider.getRtExpiration());

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
    }
}
