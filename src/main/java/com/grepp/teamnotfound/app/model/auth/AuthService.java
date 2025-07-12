package com.grepp.teamnotfound.app.model.auth;

import com.grepp.teamnotfound.app.model.auth.payload.LoginCommand;
import com.grepp.teamnotfound.app.model.auth.token.RefreshTokenService;
import com.grepp.teamnotfound.app.model.auth.token.dto.AccessTokenDto;
import com.grepp.teamnotfound.app.model.auth.token.dto.TokenDto;
import com.grepp.teamnotfound.app.model.auth.token.entity.RefreshToken;
import com.grepp.teamnotfound.app.model.auth.token.entity.UserBlackList;
import com.grepp.teamnotfound.app.model.auth.token.repository.UserBlackListRepository;
import com.grepp.teamnotfound.infra.auth.token.JwtProvider;
import com.grepp.teamnotfound.infra.auth.token.code.GrantType;
import com.grepp.teamnotfound.infra.error.exception.AuthException;
import com.grepp.teamnotfound.infra.error.exception.code.AuthErrorCode;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserBlackListRepository userBlackListRepository;


    // TODO 인증 기준 email -> userId ref 수정의 시작점
    public TokenDto login(LoginCommand request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(),
                        request.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return processTokenLogin(authentication.getName());
    }

    private TokenDto processTokenLogin(String email) {

        userBlackListRepository.deleteById(email);

        AccessTokenDto accessToken = jwtProvider.generateAccessToken(email);
        RefreshToken refreshToken = refreshTokenService.saveWithAtId(accessToken.getId());

        return TokenDto.builder()
                .accessToken(accessToken.getToken())
                .refreshToken(refreshToken.getToken())
                .grantType(GrantType.BEARER)
                .atExpiresIn(jwtProvider.getAtExpiration())
                .rtExpiresIn(jwtProvider.getRtExpiration())
                .build();
    }

    @Transactional
    public void logout(String accessToken) {
        Claims claims = jwtProvider.parseClaims(accessToken);
        String userEmail = claims.getSubject();
        String accessTokenId = claims.getId();

        // 0. 블랙리스트 확인
        if(userBlackListRepository.existsById(userEmail)){
            throw new AuthException(AuthErrorCode.ALREADY_LOGGED_OUT);
        }

        // 1. refreshToken 삭제
        refreshTokenService.deleteByAccessTokenId(accessTokenId);

        // 2. accessToken 블랙리스트에 추가 (남은 시간 계산)
        long remainingExpirationSeconds = (claims.getExpiration().getTime() - System.currentTimeMillis()) / 1000;
        if (remainingExpirationSeconds > 0) {
            userBlackListRepository.save(new UserBlackList(userEmail, remainingExpirationSeconds));
        }
    }


}
