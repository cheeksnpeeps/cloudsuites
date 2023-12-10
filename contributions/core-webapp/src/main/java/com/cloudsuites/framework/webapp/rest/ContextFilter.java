package com.cloudsuites.framework.webapp.rest;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;
import org.slf4j.MDC;
import java.io.IOException;
import java.util.UUID;

@WebFilter(urlPatterns = "/api/*")
public class ContextFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            try {
                String correlationId = getCorrelationId();
                MDC.put("correlationId", correlationId);
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.addHeader("correlationId", correlationId);
                chain.doFilter(request, response);
            } finally {
                MDC.remove("CorrelationId");
            }
    }

    private String getCorrelationId() {
        return UUID.randomUUID().toString();
    }
}
