package com.florian935.responder.rsocketjwt;

import com.florian935.responder.rsocketjwt.domain.HelloRequest;
import com.florian935.responder.rsocketjwt.domain.HelloRequests;
import com.florian935.responder.rsocketjwt.domain.HelloResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    public void testRequestResponse() {

        final Mono<HelloResponse> result = requester
                .route("request-response")
                .data(new HelloRequest("1"))
                .retrieveMono(HelloResponse.class);

        StepVerifier
                .create(result)
                .consumeNextWith(helloResponse -> {
                    assertThat(helloResponse.getId()).isEqualTo("1");
                    assertThat(helloResponse.getValue()).isEqualTo("Bonjour");
                })
                .verifyComplete();
    }

    @Test
    public void testRequestGetsStream() {

        final Flux<HelloResponse> result = requester
                .route("request-stream")
                .data(new HelloRequests(List.of("0", "1")))
                .retrieveFlux(HelloResponse.class);

        StepVerifier
                .create(result)
                .consumeNextWith(helloResponse -> {
                    assertThat(helloResponse.getId()).isEqualTo("0");
                    assertThat(helloResponse.getValue()).isEqualTo("Hello");
                })
                .consumeNextWith(helloResponse -> {
                    assertThat(helloResponse.getId()).isEqualTo("1");
                    assertThat(helloResponse.getValue()).isEqualTo("Bonjour");
                })
                .thenCancel()
                .verify();
    }

    @Test
    public void testChannel() {

        final Flux<HelloRequest> payload1 = Flux
                .just(new HelloRequest("0"))
                .delayElements(ofSeconds(0));
        final Flux<HelloRequest> payload2 = Flux
                .just(new HelloRequest("1"))
                .delayElements(ofSeconds(2));
        final Flux<HelloRequest> finalPayload = Flux.concat(payload1, payload2);

        Flux<HelloResponse> result = requester
                .route("channel")
                .data(finalPayload)
                .retrieveFlux(HelloResponse.class);

        StepVerifier
                .create(result)
                .consumeNextWith(helloResponse -> {
                    assertThat(helloResponse.getId()).isEqualTo("0");
                    assertThat(helloResponse.getValue()).isEqualTo("Hello");
                })
                .consumeNextWith(helloResponse -> {
                    assertThat(helloResponse.getId()).isEqualTo("1");
                    assertThat(helloResponse.getValue()).isEqualTo("Bonjour");
                })
                .thenCancel()
                .verify();
    }
}
