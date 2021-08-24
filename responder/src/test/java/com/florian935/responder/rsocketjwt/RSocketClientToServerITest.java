package com.florian935.responder.rsocketjwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.florian935.responder.rsocketjwt.domain.HelloRequest;
import com.florian935.responder.rsocketjwt.domain.HelloRequests;
import com.florian935.responder.rsocketjwt.domain.HelloResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.time.Duration.ofSeconds;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RSocketClientToServerITest {

    private final static MimeType BEARER_MIMETYPE = MimeTypeUtils.parseMimeType("message/x.rsocket.authentication.bearer.v0");
    private static RSocketRequester requester;

    @BeforeAll
    public static void setupOnce(@Autowired RSocketRequester.Builder builder,
                                 @LocalRSocketServerPort Integer port) {

        Hooks.onErrorDropped(error -> {});
        requester = builder
                .setupMetadata(
                        generateToken("USER",
                                "user",
                                "a1153ad6-a48b-46e5-88d4-f8284d69a6c8"
                        ),
                        BEARER_MIMETYPE)
                .tcp("localhost", port);
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
                .route("request-response.1")
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
                .metadata(generateToken("ADMIN",
                                "admin",
                                "28fbf5ca-13f7-4b9d-8a75-2ef397cad1c5"
                        ),
                        BEARER_MIMETYPE)
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

    private static String generateToken(String role, String username, String userId) {
        final Instant now = Instant.now();
        final Instant instant = now.plus(15, MINUTES);
        final String tokenId = UUID.randomUUID().toString();

        return JWT.create()
                .withJWTId(tokenId)
                .withSubject(userId)
                .withClaim("roles", role)
                .withClaim("username", username)
                .withExpiresAt(Date.from(instant))
                .sign(Algorithm.HMAC512("dfg39wLJ92kdI29084JJQjhsj98ksdfKSJnk91Kkjb87GGb898nbBbBBBbsdfkze2KFjksdfDNFSK"));
    }
}
