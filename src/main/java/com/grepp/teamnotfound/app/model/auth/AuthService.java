package com.grepp.teamnotfound.app.model.auth;

import com.grepp.teamnotfound.app.model.auth.domain.Principal;
import com.grepp.teamnotfound.app.model.auth.dto.LoginCommand;
import com.grepp.teamnotfound.app.model.auth.dto.LoginResult;
import com.grepp.teamnotfound.app.model.auth.token.RefreshTokenService;
import com.grepp.teamnotfound.app.model.auth.token.dto.AccessTokenDto;
import com.grepp.teamnotfound.app.model.auth.token.dto.TokenDto;
import com.grepp.teamnotfound.app.model.auth.token.entity.RefreshToken;
import com.grepp.teamnotfound.app.model.auth.token.entity.TokenBlackList;
import com.grepp.teamnotfound.app.model.auth.token.repository.TokenBlackListRepository;
import com.grepp.teamnotfound.app.model.user.UserService;
import com.grepp.teamnotfound.app.model.user.entity.User;
import com.grepp.teamnotfound.infra.auth.token.JwtProvider;
import com.grepp.teamnotfound.infra.error.exception.AuthException;
import com.grepp.teamnotfound.infra.error.exception.code.AuthErrorCode;
import com.grepp.teamnotfound.infra.error.exception.code.UserErrorCode;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlackListRepository tokenBlackListRepository;


    public LoginResult login(LoginCommand request) {
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException(UserErrorCode.USER_NOT_FOUND));

        if (!user.getRole().isUser()) {
            throw new AuthException(AuthErrorCode.NOT_USER_LOGIN);
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        request.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenDto tokenDto = processTokenLogin(((Principal) authentication.getPrincipal()).getUserId());

        return LoginResult.builder()
                .userId(user.getUserId())
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .grantType(tokenDto.getGrantType())
                .atExpiresIn(tokenDto.getAtExpiresIn())
                .rtExpiresIn(tokenDto.getRtExpiresIn())
                .build();
    }

    public LoginResult adminLogin(LoginCommand request) {
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException(UserErrorCode.USER_NOT_FOUND));

        if (!user.getRole().isAdmin()) {
            throw new AuthException(AuthErrorCode.NOT_ADMIN);
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        request.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenDto tokenDto = processTokenLogin(((Principal) authentication.getPrincipal()).getUserId());

        return LoginResult.builder()
                .userId(user.getUserId())
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .grantType(tokenDto.getGrantType())
                .atExpiresIn(tokenDto.getAtExpiresIn())
                .rtExpiresIn(tokenDto.getRtExpiresIn())
                .build();
    }

    public TokenDto processTokenLogin(Long userId) {

        AccessTokenDto accessToken = jwtProvider.generateAccessToken(userId);
        RefreshToken refreshToken = refreshTokenService.saveWithAtId(accessToken.getId());

        return TokenDto.of(accessToken.getToken(), refreshToken.getToken(), jwtProvider);
    }

    @Transactional
    public void logout(String accessToken) {
        Claims claims = jwtProvider.parseClaims(accessToken);

        if (claims.getExpiration().before(new Date())) {
            throw new AuthException(AuthErrorCode.TOKEN_EXPIRED);
        }

        String accessTokenId = claims.getId();

        // 0. 블랙리스트 확인
        if(tokenBlackListRepository.existsById(accessTokenId)){
            throw new AuthException(AuthErrorCode.ALREADY_LOGGED_OUT);
        }

        // 1. refreshToken 삭제
        refreshTokenService.deleteByAccessTokenId(accessTokenId);

        // 2. accessToken 블랙리스트에 추가 (남은 시간 계산)
        long remainingExpirationSeconds = (claims.getExpiration().getTime() - System.currentTimeMillis()) / 1000;
        if (remainingExpirationSeconds > 0) {
            tokenBlackListRepository.save(new TokenBlackList(accessTokenId, remainingExpirationSeconds));
        }
    }


}
