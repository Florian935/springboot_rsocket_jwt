package com.florian935.responder.rsocketjwt.configuration.jwt;

import com.auth0.jwt.JWT;
import com.florian935.responder.rsocketjwt.service.UserService;
import com.florian935.responder.rsocketjwt.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class HelloReactiveJwtDecoder implements ReactiveJwtDecoder {

    final ReactiveJwtDecoder reactiveJwtDecoder;
    UserService userService = BeanUtils.getBean(UserService.class);

    @Override
    public Mono<Jwt> decode(String token) throws JwtException {

        final String id = JWT.decode(token).getSubject();

        return Mono.just(id)
                .flatMap(userId -> userService.findById(userId))
                .switchIfEmpty(Mono.error(
                        new JwtException(
                                "No user founded with the user ID sended in the JWT !")))
                .then(reactiveJwtDecoder.decode(token));
    }
}
