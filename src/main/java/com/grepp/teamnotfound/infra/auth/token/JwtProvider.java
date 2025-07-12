package com.grepp.teamnotfound.infra.auth.token;

import com.grepp.teamnotfound.app.model.auth.domain.Principal;
import com.grepp.teamnotfound.app.model.auth.token.dto.AccessTokenDto;
import com.grepp.teamnotfound.app.model.auth.UserDetailsServiceImpl;
import com.grepp.teamnotfound.infra.auth.token.code.TokenType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Getter
    @Value("${jwt.access-expiration}")
    private long atExpiration;

    @Getter
    @Value("${jwt.refresh-expiration}")
    private long rtExpiration;

    private final UserDetailsServiceImpl userDetailsService;
    private SecretKey key;


    public SecretKey getKey(){
        if(key == null){
            String base64Key = Base64.getEncoder().encodeToString(secretKey.getBytes());
            key = Keys.hmacShaKeyFor(base64Key.getBytes(StandardCharsets.UTF_8));
        }
        return key;
    }

    // 0. jwtAuthenticationFilter에서 authentication(인증객체)로 accessToken 제작하는 로직
    public AccessTokenDto generateAccessToken(Authentication authentication){
        return generateAccessToken(authentication.getName());
    }

    // 1. accessToken 생성
    public AccessTokenDto generateAccessToken(String username){
        String id = UUID.randomUUID().toString();
        long now = new Date().getTime();
        Date atExpiresIn = new Date(now + atExpiration);
        String accessToken = Jwts.builder()
                .subject(username)
                .id(id)
                .expiration(atExpiresIn)
                .signWith(getKey())
                .compact();

        return AccessTokenDto.builder()
                .id(id)
                .token(accessToken)
                .expiresIn(atExpiration)
                .build();
    }

    // 3. token 검증 (만료 검증은 Exception 만 터트려서 filter에서 재발급 받도록 함)
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parse(token);
            return true;
        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            throw new JwtException("유효하지 않은 토큰입니다");
        }
    }


    // 4. 토큰 내 정보 추출
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(accessToken).getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // 5. Authentication 제작
    public Authentication generateAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        List<? extends GrantedAuthority> authorities = userDetailsService.findAuthorities(claims.getSubject());

        Principal principal = new Principal(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }


    // 6. 토큰 추출 resolver
    public String resolveToken(HttpServletRequest request, TokenType tokenType) {
        String headerToken = request.getHeader("Authorization");
        if (headerToken != null && headerToken.startsWith("Bearer")) {
            return headerToken.substring(7);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        return Arrays.stream(cookies)
                .filter(e -> e.getName().equals(tokenType.name()))
                .map(Cookie::getValue).findFirst()
                .orElse(null);
    }
}
