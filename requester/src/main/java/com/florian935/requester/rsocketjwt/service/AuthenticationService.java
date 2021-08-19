package com.florian935.requester.rsocketjwt.service;

import com.florian935.requester.rsocketjwt.domain.CredentialRequest;
import com.florian935.requester.rsocketjwt.domain.CredentialResponse;
import reactor.core.publisher.Mono;

public interface AuthenticationService {

    Mono<CredentialResponse> authenticate(CredentialRequest credential);
}
