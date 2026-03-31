package me.splleat.messengerproject.infrastructure.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.global.exception.ErrorCode;
import me.splleat.messengerproject.global.response.ApiErrorResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ErrorCode errorCode = (ErrorCode) request.getAttribute("error");

        if (errorCode == null) {
            errorCode = ErrorCode.UNAUTHORIZED;
        }

        ApiErrorResponse errorResponse = ApiErrorResponse.from(errorCode);

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
