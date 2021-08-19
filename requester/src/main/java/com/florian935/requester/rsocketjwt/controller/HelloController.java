package com.florian935.requester.rsocketjwt.controller;

import com.florian935.requester.rsocketjwt.domain.HelloRequest;
import com.florian935.requester.rsocketjwt.domain.HelloRequests;
import com.florian935.requester.rsocketjwt.domain.HelloResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.florian935.requester.rsocketjwt.utils.MimeTypeProperties.BEARER_MIMETYPE;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

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

    @GetMapping(path = "request-response", produces = APPLICATION_JSON_VALUE)
    Mono<HelloResponse> requestResponse(@RequestHeader("Authorization") String token,
                                        @RequestBody HelloRequest helloRequest) {

        return rSocketRequester
                .route("request-response")
                .metadata(token, BEARER_MIMETYPE)
                .data(helloRequest)
                .retrieveMono(HelloResponse.class);
    }

    @GetMapping(path = "request-stream", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<HelloResponse> requestStream(@RequestHeader("Authorization") String token,
                                      @RequestBody HelloRequests helloRequests) {

        return rSocketRequester
                .route("request-stream")
                .metadata(token, BEARER_MIMETYPE)
                .data(helloRequests)
                .retrieveFlux(HelloResponse.class);
    }

    @GetMapping(path = "channel", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<HelloResponse> requestChannel(@RequestHeader("Authorization") String token) {

        final Flux<HelloRequest> requestFlux = Flux.just(
                new HelloRequest("0"),
                new HelloRequest("1"),
                new HelloRequest("2"),
                new HelloRequest("3")
        );

        return rSocketRequester
                .route("channel")
                .metadata(token, BEARER_MIMETYPE)
                .data(requestFlux)
                .retrieveFlux(HelloResponse.class);
    }
}
