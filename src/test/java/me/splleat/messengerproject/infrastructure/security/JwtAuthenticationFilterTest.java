package me.splleat.messengerproject.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAuthenticationFilterTest {

    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
            null,
            List.of("/actuator/health", "/auth/login", "/swagger-ui")
    );

    @ParameterizedTest
    @ValueSource(strings = {"/actuator/health", "/auth/login", "/swagger-ui/index.html"})
    @DisplayName("shouldNotFilter: public path와 정확히 일치하거나 하위 경로면 필터를 건너뛴다.")
    void shouldNotFilter_WhenExactOrSubPath_ReturnsTrue(String uri) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", uri);

        assertThat(filter.shouldNotFilter(request))
                .isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/actuator/healthxxxx", "/auth/loginxxxx", "/actuator/prometheus"})
    @DisplayName("shouldNotFilter: public path로 시작만 하고 실제로는 다른 경로면 필터를 건너뛰지 않는다.")
    void shouldNotFilter_WhenPrefixButNotSubPath_ReturnsFalse(String uri) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", uri);

        assertThat(filter.shouldNotFilter(request))
                .isFalse();
    }

    @Test
    @DisplayName("shouldNotFilter: 매칭되는 public path가 없으면 필터를 건너뛰지 않는다.")
    void shouldNotFilter_WhenNoMatch_ReturnsFalse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/channels");

        assertThat(filter.shouldNotFilter(request))
                .isFalse();
    }
}
