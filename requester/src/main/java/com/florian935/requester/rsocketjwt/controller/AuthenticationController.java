package com.florian935.requester.rsocketjwt.controller;

import com.florian935.requester.rsocketjwt.domain.Credentials;
import com.florian935.requester.rsocketjwt.domain.HelloUser;
import com.florian935.requester.rsocketjwt.utils.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1.0/authenticate")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AuthenticationController {

    TokenUtils tokenUtils;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    Mono<String> signIn(@RequestBody Credentials credentials) {

        final HelloUser user = HelloUser.builder()
                .userId(credentials.getLogin())
                .password(credentials.getPassword())
                .role(credentials.getRole())
                .build();
        final String token = tokenUtils.generateAccessToken(user).getToken();

        return Mono.just(token);
    }
}
