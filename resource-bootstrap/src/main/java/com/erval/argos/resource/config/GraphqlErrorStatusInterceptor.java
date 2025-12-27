package com.erval.argos.api.config;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.graphql.ResponseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.graphql.server.WebGraphQlInterceptor;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Interceptor that sets HTTP status codes for GraphQL responses containing errors.
 * GraphQL spec allows 200 with errors, but APIs often prefer 4xx/5xx.
 */
@Configuration
@Order(0)
public class GraphqlErrorStatusInterceptor {

    @Bean
    public WebGraphQlInterceptor errorStatusInterceptor() {
        return (request, chain) -> chain.next(request).doOnNext(response -> {
            List<ResponseError> errors = response.getErrors();
            if (errors.isEmpty()) {
                return;
            }

            HttpStatus status = deriveStatus(errors);

            Object servletResponse = request.getAttributes().get(HttpServletResponse.class.getName());
            if (servletResponse instanceof HttpServletResponse httpResponse) {
                httpResponse.setStatus(status.value());
                return;
            }

            Object serverResponse = request.getAttributes().get(ServerHttpResponse.class.getName());
            if (serverResponse instanceof ServerHttpResponse httpResponse) {
                httpResponse.setStatusCode(status);
            }
        });
    }

    private HttpStatus deriveStatus(List<ResponseError> errors) {
        boolean notFound = errors.stream()
                .map(ResponseError::getMessage)
                .filter(Objects::nonNull)
                .anyMatch(msg -> msg.toLowerCase(Locale.ROOT).contains("not found"));

        if (notFound) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.BAD_REQUEST;
    }
}
