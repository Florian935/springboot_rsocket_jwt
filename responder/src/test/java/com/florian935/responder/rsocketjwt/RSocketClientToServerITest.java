package com.florian935.responder.rsocketjwt;

import com.florian935.responder.rsocketjwt.domain.HelloRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
public class RSocketClientToServerITest {

    private static RSocketRequester requester;

    @BeforeAll
    public static void setupOnce(@Autowired RSocketRequester.Builder builder,
                                 @LocalRSocketServerPort Integer port) {

        Hooks.onErrorDropped(error -> {});
        requester = builder.tcp("localhost", port);
    }

    @Test
    public void testFireAndForget() {

        final Mono<Void> result = requester
                .route("fire-and-forget")
                .data(new HelloRequest("1"))
                .retrieveMono(Void.class);

        StepVerifier
                .create(result)
                .verifyComplete();
    }
}
