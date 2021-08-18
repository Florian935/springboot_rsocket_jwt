package com.florian935.requester.rsocketjwt.controller;

import com.florian935.requester.rsocketjwt.domain.Credentials;
import com.florian935.requester.rsocketjwt.domain.HelloRequest;
import com.florian935.requester.rsocketjwt.domain.HelloResponse;
import com.florian935.requester.rsocketjwt.domain.HelloUser;
import com.florian935.requester.rsocketjwt.utils.MimeTypeProperties;
import com.florian935.requester.rsocketjwt.utils.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.florian935.requester.rsocketjwt.utils.MimeTypeProperties.BEARER_MIMETYPE;
import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/v1.0/rsocket")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class HelloController {

    final RSocketRequester rSocketRequester;

    @GetMapping("fire-and-forget/{id}")
    Mono<Void> fireAndForget(@RequestHeader("Authorization") String token,
                             @PathVariable String id) {

        return rSocketRequester
                .route("fire-and-forget")
                .metadata(token, BEARER_MIMETYPE)
                .data(new HelloRequest(id))
                .send();
    }

    @GetMapping("request-response")
    Mono<HelloResponse> requestResponse(@RequestHeader("Authorization") String token,
                                        @RequestBody HelloRequest helloRequest) {

        return rSocketRequester
                .route("request-response")
                .metadata(token, BEARER_MIMETYPE)
                .data(helloRequest)
                .retrieveMono(HelloResponse.class);
    }
}
