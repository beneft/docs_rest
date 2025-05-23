package com.project.aspect;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class HttpLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("HTTP REQUEST: method={}, URI={}", request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        log.info("HTTP RESPONSE: status={}, URI={}", response.getStatus(), request.getRequestURI());
        if (ex != null) {
            log.error("EXCEPTION during request to {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        }
    }
}