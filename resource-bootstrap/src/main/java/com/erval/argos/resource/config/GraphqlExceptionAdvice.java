package com.erval.argos.resource.config;

import java.util.NoSuchElementException;

import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.ErrorType;

/**
 * Global GraphQL exception handling that maps domain errors to HTTP status codes.
 */
@ControllerAdvice
public class GraphqlExceptionAdvice {

    @GraphQlExceptionHandler
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.BAD_REQUEST)
    public GraphQLError handleIllegalArgument(IllegalArgumentException ex) {
        return GraphqlErrorBuilder.newError()
                .message(ex.getMessage())
                .errorType(ErrorType.ValidationError)
                .build();
    }

    @GraphQlExceptionHandler
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NOT_FOUND)
    public GraphQLError handleNotFound(NoSuchElementException ex) {
        return GraphqlErrorBuilder.newError()
                .message(ex.getMessage())
                .errorType(ErrorType.DataFetchingException)
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleResponseStatus(ResponseStatusException ex) {
        HttpStatus status = ex.getStatusCode() instanceof HttpStatus http ? http : HttpStatus.BAD_REQUEST;
        ErrorType errorType = status.is4xxClientError() ? ErrorType.ValidationError : ErrorType.DataFetchingException;
        return GraphqlErrorBuilder.newError()
                .message(ex.getReason() != null ? ex.getReason() : ex.getMessage())
                .errorType(errorType)
                .build();
    }

    @GraphQlExceptionHandler
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GraphQLError handleOther(Throwable ex) {
        return GraphqlErrorBuilder.newError()
                .message("Unexpected error: " + ex.getMessage())
                .errorType(ErrorType.DataFetchingException)
                .build();
    }
}
