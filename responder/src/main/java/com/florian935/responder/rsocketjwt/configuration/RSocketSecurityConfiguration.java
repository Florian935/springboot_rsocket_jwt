package com.florian935.responder.rsocketjwt.configuration;

import com.florian935.responder.rsocketjwt.utils.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;

import static com.florian935.responder.rsocketjwt.domain.HelloRole.ADMIN;
import static com.florian935.responder.rsocketjwt.domain.HelloRole.USER;
import static lombok.AccessLevel.PRIVATE;

@Configuration
@EnableRSocketSecurity
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RSocketSecurityConfiguration {

    TokenUtils tokenUtils;

    @Bean
    PayloadSocketAcceptorInterceptor authorization(RSocketSecurity rSocketSecurity) {

        return rSocketSecurity
                .authorizePayload(authorize -> authorize
                        .setup().permitAll()
                        .route("fire-and-forget").authenticated()
                        .route("request-response").hasRole(USER)
                        .route("request-stream").hasRole(ADMIN)
                        .route("channel").hasAnyRole(USER, ADMIN)
                        .anyRequest().authenticated()
                        .anyExchange().permitAll()
                )
                .jwt(jwtSpec -> {
                    jwtSpec.authenticationManager(jwtReactiveAuthenticationManager(jwtDecoder()));
                })
                .build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {

        return tokenUtils.jwtAccessTokenDecoder();
    }

    @Bean
    public JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager(ReactiveJwtDecoder reactiveJwtDecoder) {

        JwtReactiveAuthenticationManager authenticationManager = new JwtReactiveAuthenticationManager(reactiveJwtDecoder);
        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        authenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        authenticationManager.setJwtAuthenticationConverter(new ReactiveJwtAuthenticationConverterAdapter(authenticationConverter));
        return authenticationManager;
    }

    @Bean
    RSocketMessageHandler messageHandler(RSocketStrategies strategies) {

        RSocketMessageHandler rSocketMessageHandler = new RSocketMessageHandler();
        rSocketMessageHandler.getArgumentResolverConfigurer().addCustomResolver(
                new AuthenticationPrincipalArgumentResolver());
        rSocketMessageHandler.setRSocketStrategies(strategies);

        return rSocketMessageHandler;
    }
}
