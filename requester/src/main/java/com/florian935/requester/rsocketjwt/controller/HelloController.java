package com.florian935.requester.rsocketjwt.controller;

import com.florian935.requester.rsocketjwt.domain.Credentials;
import com.florian935.requester.rsocketjwt.domain.HelloRequest;
import com.florian935.requester.rsocketjwt.domain.HelloResponse;
import com.florian935.requester.rsocketjwt.domain.HelloUser;
import com.florian935.requester.rsocketjwt.utils.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.florian935.requester.rsocketjwt.domain.HelloRole.USER;
import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/v1.0")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class HelloController {

    final RSocketRequester rSocketRequester;
    final TokenUtils tokenUtils;
    final MimeType mimeType = MimeType.valueOf("message/x.rsocket.authentication.bearer.v0");
    String token;

    @GetMapping("signin")
    Mono<String> signIn(@RequestBody Credentials credentials) {

        final HelloUser user = HelloUser.builder()
                .userId(credentials.getLogin())
                .password(credentials.getPassword())
                .role(credentials.getRole())
                .build();
        token = tokenUtils.generateAccessToken(user).getToken();

        return Mono.just("Logged successfully !");
    }

    @GetMapping("fire-and-forget")
    Mono<Void> fireAndForget() {

        return rSocketRequester
                .route("fire-and-forget")
                .metadata(token, mimeType)
                .data(new HelloRequest("0"))
                .send();
    }

    @GetMapping("request-response")
    Mono<HelloResponse> requestResponse(@RequestBody HelloRequest helloRequest) {

        return rSocketRequester
                .route("request-response")
                .metadata(token, mimeType)
                .data(helloRequest)
                .retrieveMono(HelloResponse.class);
    }
}
