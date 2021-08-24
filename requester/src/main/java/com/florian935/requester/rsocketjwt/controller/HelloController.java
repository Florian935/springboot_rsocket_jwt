package com.florian935.requester.rsocketjwt.controller;

import com.florian935.requester.rsocketjwt.domain.HelloRequest;
import com.florian935.requester.rsocketjwt.domain.HelloRequests;
import com.florian935.requester.rsocketjwt.domain.HelloResponse;
import com.florian935.requester.rsocketjwt.service.HelloService;
import com.florian935.requester.rsocketjwt.utils.TypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
@RequestMapping("/api/v1.0/rsocket")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class HelloController {

    HelloService helloService;

    @GetMapping("fire-and-forget/{id}")
    Mono<Void> fireAndForget(@PathVariable String id,
                             @AuthenticationPrincipal Authentication principal) {

        final String token = extractTokenFromBearerToken(principal.getCredentials());

        return helloService.fireAndForget(token, id);
    }

    @GetMapping(path = "request-response", produces = APPLICATION_JSON_VALUE)
    Mono<HelloResponse> requestResponse(@RequestBody HelloRequest helloRequest,
                                        @AuthenticationPrincipal Authentication principal) {

        final String token = extractTokenFromBearerToken(principal.getCredentials());

        return helloService.requestResponse(token, helloRequest);
    }

    @GetMapping(path = "request-stream", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<HelloResponse> requestStream(@RequestBody HelloRequests helloRequests,
                                      @AuthenticationPrincipal Authentication principal) {

        final String token = extractTokenFromBearerToken(principal.getCredentials());

        return helloService.requestStream(token, helloRequests);
    }

    @GetMapping(path = "channel", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<HelloResponse> requestChannel(@AuthenticationPrincipal Authentication principal) {

        final String token = extractTokenFromBearerToken(principal.getCredentials());

        return helloService.requestChannel(token);
    }

    private String extractTokenFromBearerToken(Object token) {

        return TypeConverter.fromObjectToString(token);
    }
}
