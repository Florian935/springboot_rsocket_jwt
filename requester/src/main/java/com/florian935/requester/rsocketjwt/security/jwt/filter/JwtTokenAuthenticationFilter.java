package com.florian935.requester.rsocketjwt.security.jwt.filter;

import com.florian935.requester.rsocketjwt.security.jwt.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class JwtTokenAuthenticationFilter implements WebFilter {

    public static String BEARER_PREFIX = "Bearer ";

    JwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String token = resolveToken(exchange.getRequest());

        if (StringUtils.hasText(token) && jwtTokenProvider.isValidToken(token)) {
            Authentication authentication = this.jwtTokenProvider.getAuthenticationFromToken(token);

            return chain
                    .filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        }

        return chain.filter(exchange);
    }

    private String resolveToken(ServerHttpRequest request) {

        String bearerToken = request.getHeaders().getFirst(AUTHORIZATION);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}
