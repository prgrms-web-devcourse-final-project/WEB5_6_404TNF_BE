package com.grepp.teamnotfound.infra.auth.token.filter;

import com.grepp.teamnotfound.infra.error.exception.AuthException;
import com.grepp.teamnotfound.infra.error.exception.CommonException;
import com.grepp.teamnotfound.infra.error.exception.code.AuthErrorCode;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@Slf4j
public class AuthExceptionFilter extends OncePerRequestFilter {


    private final HandlerExceptionResolver handlerExceptionResolver;

    public AuthExceptionFilter(
            @Qualifier("handlerExceptionResolver")
            HandlerExceptionResolver handlerExceptionResolver) {
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{
            filterChain.doFilter(request, response);
        } catch (CommonException e){
            handlerExceptionResolver.resolveException(request, response, null, e);
        } catch (JwtException e) {
            handlerExceptionResolver.resolveException(request, response, null,
                    new AuthException(AuthErrorCode.INVALID_TOKEN));
        } catch (Exception e){
            handlerExceptionResolver.resolveException(request, response, null,
                    new AuthException(AuthErrorCode.UNAUTHENTICATED));
        }
    }

    // swagger 프리플라이트 방지
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }
}
