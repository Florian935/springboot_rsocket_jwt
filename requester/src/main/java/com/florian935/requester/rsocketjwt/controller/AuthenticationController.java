package com.florian935.requester.rsocketjwt.controller;

import com.florian935.requester.rsocketjwt.domain.CredentialRequest;
import com.florian935.requester.rsocketjwt.domain.CredentialResponse;
import com.florian935.requester.rsocketjwt.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1.0/authenticate")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    Mono<CredentialResponse> authenticate(@RequestBody CredentialRequest credential) {

        return authenticationService.authenticate(credential);
    }
}
