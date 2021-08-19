package com.florian935.responder.rsocketjwt.controller;

import com.florian935.responder.rsocketjwt.domain.HelloRequest;
import com.florian935.responder.rsocketjwt.domain.HelloRequests;
import com.florian935.responder.rsocketjwt.domain.HelloResponse;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static java.time.Duration.ofMillis;
import static lombok.AccessLevel.PRIVATE;

@Controller
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class HelloController {

    List<String> HELLO_LIST = Arrays.asList("Hello", "Bonjour", "Hola", "こんにちは", "Ciao", "안녕하세요");

    @MessageMapping("fire-and-forget")
    Mono<Void> fireAndForget(@Payload HelloRequest helloRequest) {

        log.info(">> [FireAndForget] FNF: {}", helloRequest.getId());

        return Mono.empty();
    }

    @PreAuthorize("hasRole('USER')")
    @MessageMapping("request-response.{id}")
    Mono<HelloResponse> requestAndResponse(@DestinationVariable String id) {
        log.info(" >> [Request-Response] data: {}", id);

        return Mono.just(getHello(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @MessageMapping("request-stream")
    Flux<HelloResponse> requestStream(@Payload HelloRequests helloRequests) {

        log.info(">> [Request-Stream] data:{}", helloRequests);
        List<String> ids = helloRequests.getIds();

        return Flux.fromIterable(ids)
                .delayElements(ofMillis(500))
                .map(this::getHello);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @MessageMapping("channel")
    Flux<HelloResponse> requestChannel(@Payload Flux<HelloRequest> requests) {

        return Flux.from(requests)
                .doOnNext(message -> log.info(">> [Request-Channel] data: {}", message))
                .map(message -> getHello(message.getId()));
    }

    private HelloResponse getHello(String id) {

        int index;
        try {
            index = Integer.parseInt(id);
        } catch (NumberFormatException ignored) {
            index = 0;
        }

        if (index > 5) {
            return new HelloResponse(id, "你好");
        }

        return new HelloResponse(id, HELLO_LIST.get(index));
    }

    private HelloResponse getHello(int index) {

        return new HelloResponse(String.valueOf(index), HELLO_LIST.get(index));
    }
}
