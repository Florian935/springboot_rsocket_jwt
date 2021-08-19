package com.florian935.requester.rsocketjwt.configuration;

import com.florian935.requester.rsocketjwt.security.jwt.filter.JwtTokenAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Configuration
@EnableWebFluxSecurity
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class WebSecurityConfiguration {

    static String API_PATH = "/api/v1.0/authenticate";
    private final JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter;

    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http,
                                                ReactiveAuthenticationManager reactiveAuthenticationManager) {

        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(this::configureAuthorizeExchangeSpec)
                .authenticationManager(reactiveAuthenticationManager)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .requestCache().requestCache(NoOpServerRequestCache.getInstance())
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(WebSecurityConfiguration::commence)
                .accessDeniedHandler(WebSecurityConfiguration::handle)
                .and()
                .addFilterAt(jwtTokenAuthenticationFilter, SecurityWebFiltersOrder.HTTP_BASIC)
                .build();
    }

    private void configureAuthorizeExchangeSpec(ServerHttpSecurity.AuthorizeExchangeSpec exchange) {

        exchange
                .pathMatchers(API_PATH).permitAll()
                .anyExchange().authenticated();
    }

    private static Mono<Void> commence(ServerWebExchange response, AuthenticationException error) {

        return Mono.fromRunnable(() -> response
                .getResponse().setStatusCode(UNAUTHORIZED));
    }

    private static Mono<Void> handle(ServerWebExchange response, AccessDeniedException error) {

        return Mono.fromRunnable(() -> response
                .getResponse().setStatusCode(FORBIDDEN));
    }
}
