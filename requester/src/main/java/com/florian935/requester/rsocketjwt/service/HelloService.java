package com.florian935.requester.rsocketjwt.service;

import com.florian935.requester.rsocketjwt.domain.HelloRequest;
import com.florian935.requester.rsocketjwt.domain.HelloRequests;
import com.florian935.requester.rsocketjwt.domain.HelloResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface HelloService {

    Mono<Void> fireAndForget(String token, String id);

    Mono<HelloResponse> requestResponse(String token, HelloRequest helloRequest);

    Flux<HelloResponse> requestStream(String token, HelloRequests helloRequests);

    Flux<HelloResponse> requestChannel(String token);
}
