package com.florian935.responder.rsocketjwt.configuration.jwt;

import com.auth0.jwt.JWT;
import com.florian935.responder.rsocketjwt.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

import static lombok.AccessLevel.PRIVATE;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class HelloReactiveJwtDecoder implements ReactiveJwtDecoder {

    ReactiveJwtDecoder reactiveJwtDecoder;
    UserService userService;

    @Override
    public Mono<Jwt> decode(String token) throws JwtException {

        final String id = JWT.decode(token).getSubject();

        return Mono.just(id)
                .flatMap(userService::findById)
                .switchIfEmpty(Mono.error(
                        new JwtException(
                                "No user founded with the user ID sended in the JWT !")))
                .then(reactiveJwtDecoder.decode(token));
    }
}
