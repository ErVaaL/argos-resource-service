package com.erval.argos.resource.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;

/**
 * Basic security configuration allowing open access to GraphQL and internal token endpoint.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

//    private final KeyPair internalKeyPair;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        var resolver = new DefaultBearerTokenResolver();
//        resolver.setBearerTokenHeaderName("Secured-Authorization");
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/internal/oauth/**").permitAll()
                .requestMatchers("/graphql").permitAll()
                .anyRequest().authenticated())
            .formLogin(AbstractHttpConfigurer::disable);
//            .oauth2ResourceServer(oauth -> oauth
//                .bearerTokenResolver(resolver)
//                .jwt(jwt -> jwt
//                    .decoder(jwtDecoder())
//                    .jwtAuthenticationConverter(jwtAuthConverter())
//                ));
        return http.build();
    }

//    @Bean
//    public JwtDecoder jwtDecoder() {
//        return NimbusJwtDecoder.withPublicKey((RSAPublicKey) internalKeyPair.getPublic()).build();
//    }

//    @Bean
//    public JwtAuthenticationConverter jwtAuthConverter() {
//        var converter = new JwtAuthenticationConverter();
//        var gac = new JwtGrantedAuthoritiesConverter();
//        gac.setAuthoritiesClaimName("scp");
//        gac.setAuthorityPrefix("");
//        converter.setJwtGrantedAuthoritiesConverter(gac);
//        return converter;
//    }
}
