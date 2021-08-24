package com.florian935.requester.rsocketjwt.service.implementation;

import com.florian935.requester.rsocketjwt.domain.HelloRequest;
import com.florian935.requester.rsocketjwt.domain.HelloRequests;
import com.florian935.requester.rsocketjwt.domain.HelloResponse;
import com.florian935.requester.rsocketjwt.service.HelloService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.florian935.requester.rsocketjwt.utils.MimeTypeProperties.BEARER_MIMETYPE;
import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class HelloServiceImpl implements HelloService {

    RSocketRequester rSocketRequester;

    @Override
    public Mono<Void> fireAndForget(String token, String id) {

        return rSocketRequester
                .route("fire-and-forget")
                .metadata(token, BEARER_MIMETYPE)
                .data(new HelloRequest(id))
                .send();
    }

    @Override
    public Mono<HelloResponse> requestResponse(String token, HelloRequest helloRequest) {

        final String route = String.format("request-response.%s", helloRequest.getId());

        return rSocketRequester
                .route(route)
                .metadata(token, BEARER_MIMETYPE)
                .retrieveMono(HelloResponse.class);
    }

    @Override
    public Flux<HelloResponse> requestStream(String token, HelloRequests helloRequests) {

        return rSocketRequester
                .route("request-stream")
                .metadata(token, BEARER_MIMETYPE)
                .data(helloRequests)
                .retrieveFlux(HelloResponse.class);
    }

    @Override
    public Flux<HelloResponse> requestChannel(String token) {

        final Flux<HelloRequest> requestFlux = getHelloRequests();

        return rSocketRequester
                .route("channel")
                .metadata(token, BEARER_MIMETYPE)
                .data(requestFlux)
                .retrieveFlux(HelloResponse.class);
    }

    Flux<HelloRequest> getHelloRequests() {

        return Flux.just(
                new HelloRequest("0"),
                new HelloRequest("1"),
                new HelloRequest("2"),
                new HelloRequest("3")
        );
    }
}
