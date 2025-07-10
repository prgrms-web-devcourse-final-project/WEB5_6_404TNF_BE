package com.grepp.teamnotfound.infra.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openApiSpec() {
        return new OpenAPI()
                .info(new Info()
                        .title("API 문서")
                        .description("API 명세입니다. 에러 코드는 [링크]를 참조해 주세요.")
                        .version("v1.0.0"))
                // todo 배포할 때만 허용...
//                .servers(List.of(
//                        new Server().url("https://mungnote-172598302113.asia-northeast3.run.app")
//                ))
                .components(
                        new Components()
                                .addSecuritySchemes("bearerAuth"
                                        , new SecurityScheme()
                                                .name("bearerAuth")
                                                .type(
                                                        SecurityScheme.Type.HTTP)
                                                .scheme(
                                                        "bearer")
                                                .bearerFormat(
                                                        "JWT")
                                                .description(
                                                        "JWT 토큰을 입력하세요. Bearer 는 생략하세요")
                                ))
                .addSecurityItem(new SecurityRequirement().addList("404TNF"));
    }
}
