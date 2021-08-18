package com.florian935.requester.rsocketjwt.configuration;

import com.florian935.requester.rsocketjwt.domain.HelloUser;
import com.florian935.requester.rsocketjwt.utils.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.MimeType;

import static com.florian935.requester.rsocketjwt.domain.HelloRole.ADMIN;
import static com.florian935.requester.rsocketjwt.domain.HelloRole.USER;
import static lombok.AccessLevel.PRIVATE;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RSocketConfiguration {

    TokenUtils tokenUtils;
    MimeType mimeType = MimeType.valueOf("message/x.rsocket.authentication.bearer.v0");


    @Bean
    RSocketRequester rSocketRequester(RSocketRequester.Builder builder) {
        HelloUser user = HelloUser.builder().userId("user").password("pass").role(USER).build();
        HelloUser admin = HelloUser.builder().userId("admin").password("pass").role(ADMIN).build();

        String userToken = tokenUtils.generateAccessToken(user).getToken();
        String adminToken = tokenUtils.generateAccessToken(admin).getToken();

        return builder
//                .setupMetadata(userToken, mimeType)
                .tcp("localhost", 7000);
    }
}
