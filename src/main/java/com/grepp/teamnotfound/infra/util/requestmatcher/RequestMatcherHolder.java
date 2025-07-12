package com.grepp.teamnotfound.infra.util.requestmatcher;

import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Component
public class RequestMatcherHolder {

    private record RequestInfo(HttpMethod method, String pattern, @Nullable String minRole) {
    }

    private static final List<RequestInfo> REQUEST_INFO_LIST = List.of(

            // 1. common
            new RequestInfo(null, "/", null),
            new RequestInfo(null, "/error", null),
            new RequestInfo(null, "/favicon.ico", null),

            // 2. auth 로그인
            new RequestInfo(POST,"/api/auth/v1/login", null),
            new RequestInfo(POST, "/api/auth/v1/admin/login", null),

            // 3. auth 회원가입
            new RequestInfo(POST, "/api/auth/v1/register", null),
            new RequestInfo(POST, "/api/auth/v1/admin/register", null),
            new RequestInfo(GET, "/api/auth/v1/check-nickname", null),
            new RequestInfo(GET, "/api/auth/v1/check-email", null),
            new RequestInfo(POST, "/api/auth/v1/email-verifications", null),
            new RequestInfo(POST, "/api/auth/v1/email-verifications/verify", null),

            // 3. admin
            new RequestInfo(null, "/api/admin/**", "ADMIN"),

            // 4. swagger
            new RequestInfo(null, "/swagger-ui.html", null),
            new RequestInfo(null, "/swagger-ui/**", null),
            new RequestInfo(null, "/v3/api-docs/**", null),
            new RequestInfo(null, "/swagger-resources/**", null),
            new RequestInfo(null, "/webjars/**", null),

            // 5. 기타 개발용 open page
            // GET /**
            new RequestInfo(GET, "/**", null)
    );

    // 캐싱 맵
    private final ConcurrentHashMap<String, RequestMatcher> reqMatcherCacheMap = new ConcurrentHashMap<>();

    public RequestMatcher getRequestMatchersByMinRole(@Nullable String minRole) {
        var key = getKeyByRole(minRole);
        return reqMatcherCacheMap.computeIfAbsent(key, k ->
                new OrRequestMatcher(REQUEST_INFO_LIST.stream()

                        // minRole이 불일치 하면 필터링
                        .filter(reqInfo -> Objects.equals(reqInfo.minRole(), minRole))
                        .map(reqInfo -> {
                            // httpMethod == null -> 모든 메소드를 매칭하는 Ant 생성
                            if (reqInfo.method() == null) {
                                return new AntPathRequestMatcher(reqInfo.pattern());
                            } else {
                                // httpMethod가 있을 경우 해당 메소드만 매칭 antPath 생성
                                return new AntPathRequestMatcher(reqInfo.pattern(), reqInfo.method().name());
                            }
                        })
                        .collect(Collectors.toList())));
    }

    private String getKeyByRole(@Nullable String minRole) {
        return minRole == null ? "VISITOR" : minRole;
    }
}
