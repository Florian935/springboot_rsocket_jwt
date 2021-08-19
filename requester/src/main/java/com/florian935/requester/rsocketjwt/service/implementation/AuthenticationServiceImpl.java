package com.florian935.requester.rsocketjwt.service.implementation;

import com.florian935.requester.rsocketjwt.domain.CredentialRequest;
import com.florian935.requester.rsocketjwt.domain.CredentialResponse;
import com.florian935.requester.rsocketjwt.domain.HelloUser;
import com.florian935.requester.rsocketjwt.service.AuthenticationService;
import com.florian935.requester.rsocketjwt.utils.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {

    RSocketRequester rSocketRequester;
    TokenUtils tokenUtils;

    @Override
    public Mono<CredentialResponse> authenticate(CredentialRequest credential) {

        final HelloUser user = HelloUser.builder()
                .userId(credential.getLogin())
                .password(credential.getPassword())
                .role(credential.getRole())
                .build();
        final String token = tokenUtils.generateAccessToken(user).getToken();

        return Mono.just(new CredentialResponse(token));
    }
}
