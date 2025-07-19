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

            // 2. auth 로그인/회원가입
            new RequestInfo(POST,"/api/auth/**", null),
            new RequestInfo(GET,"/api/auth/**", null),

            // 4. swagger
            new RequestInfo(null, "/swagger-ui.html", null),
            new RequestInfo(null, "/swagger-ui/**", null),
            new RequestInfo(null, "/v3/api-docs/**", null),
            new RequestInfo(null, "/swagger-resources/**", null),
            new RequestInfo(null, "/webjars/**", null),


            // 소셜 로그인 로컬 테스트
            new RequestInfo(GET, "/social/login", null),
            new RequestInfo(GET, "/error/**", null),

            // 5. 기타 개발용 open page
            // GET /**
            new RequestInfo(GET, "/", null),
            new RequestInfo(GET, "/api/community/**", null),
//            new RequestInfo(GET, "/api/life-record/**", null),
//            new RequestInfo(GET, "/api/mypage/**", null),
            new RequestInfo(GET, "/api/profile/**", null)
//            new RequestInfo(GET, "/api/vaccines/**", null),
//            new RequestInfo(GET, "/api/v1/dashboard/**", null)
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
