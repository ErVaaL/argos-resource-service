package com.erval.argos.resource.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

/**
 * GraphQL runtime configuration for custom scalars.
 */
@Configuration
public class GraphqlConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer(GraphQLScalarType longScalar) {
        return builder -> builder.scalar(longScalar);
    }

    @Bean
    public GraphQLScalarType longScalar() {
        return GraphQLScalarType.newScalar()
                .name("Long")
                .description("64-bit signed integer")
                .coercing(new Coercing<Long, Long>() {
                    @Override
                    public Long serialize(Object input) throws CoercingSerializeException {
                        Long value = convertToLong(input);
                        if (value == null) {
                            throw new CoercingSerializeException("Unable to serialize value as Long: " + input);
                        }
                        return value;
                    }

                    @Override
                    public Long parseValue(Object input) throws CoercingParseValueException {
                        Long value = convertToLong(input);
                        if (value == null) {
                            throw new CoercingParseValueException("Unable to parse value as Long: " + input);
                        }
                        return value;
                    }

                    @Override
                    public Long parseLiteral(Object input) throws CoercingParseLiteralException {
                        Long value = convertLiteral(input);
                        if (value == null) {
                            throw new CoercingParseLiteralException("Expected Long literal but got: " + input);
                        }
                        return value;
                    }
                })
                .build();
    }

    private Long convertToLong(Object input) {
        if (input == null) {
            return null;
        }
        if (input instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(input.toString());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Long convertLiteral(Object input) {
        if (input instanceof IntValue intValue) {
            return intValue.getValue().longValue();
        }
        if (input instanceof StringValue stringValue) {
            return convertToLong(stringValue.getValue());
        }
        if (input instanceof Value<?> value) {
            return convertToLong(value.toString());
        }
        return null;
    }
}
