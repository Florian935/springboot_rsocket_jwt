package com.florian935.requester.rsocketjwt.configuration;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;

import static lombok.AccessLevel.PRIVATE;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RSocketConfiguration {

    @Bean
    RSocketRequester rSocketRequester(RSocketRequester.Builder builder) {

        return builder.tcp("localhost", 7000);
    }
}
