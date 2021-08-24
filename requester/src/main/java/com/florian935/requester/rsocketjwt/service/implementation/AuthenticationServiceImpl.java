package com.florian935.requester.rsocketjwt.service.implementation;

import com.florian935.requester.rsocketjwt.domain.CredentialRequest;
import com.florian935.requester.rsocketjwt.domain.CredentialResponse;
import com.florian935.requester.rsocketjwt.domain.HelloUser;
import com.florian935.requester.rsocketjwt.repository.UserRepository;
import com.florian935.requester.rsocketjwt.service.AuthenticationService;
import com.florian935.requester.rsocketjwt.security.jwt.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {

    JwtTokenProvider jwtTokenProvider;
    UserRepository userRepository;
    ReactiveAuthenticationManager reactiveAuthenticationManager;

    @Override
    public Mono<CredentialResponse> authenticate(CredentialRequest credential) {

        return Mono.just(credential)
                .flatMap(this::processAuthentication)
                .filter(Objects::nonNull)
                .map(authentication -> credential.getUsername())
                .flatMap(this::findByUsername)
                .map(u -> buildUser(u, credential));
    }

    @Override
    public Mono<HelloUser> findByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    private Mono<Authentication> processAuthentication(CredentialRequest credential) {

        return reactiveAuthenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(credential.getUsername(), credential.getPassword())
        );
    }

    private CredentialResponse buildUser(HelloUser helloUser, CredentialRequest credential) {

        final HelloUser user = HelloUser.builder()
                .userId(helloUser.getUserId())
                .username(credential.getUsername())
                .password(credential.getPassword())
                .role(helloUser.getRole())
                .build();

        final String token = jwtTokenProvider.generateToken(user).getToken();

        return new CredentialResponse(token);
    }
}
