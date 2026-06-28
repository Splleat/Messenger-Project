package me.splleat.messengerproject.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class OpenApiConfig {

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Messenger API")
                        .version("v1")
                        .description("""
                                실시간 메신저 서비스 API.

                                인증이 필요한 엔드포인트 테스트 방법
                                1. `POST /auth/login` 으로 로그인 → 응답의 `accessToken` 복사
                                2. 우상단 Authorize 클릭 → 토큰 붙여넣기 (Bearer 접두 없이)

                                데모 계정: `demo@splleat.com` / `demo1234`"""))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer").bearerFormat("JWT")));
    }
}
