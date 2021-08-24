package com.florian935.responder.rsocketjwt.service;

import com.florian935.responder.rsocketjwt.domain.HelloUser;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<HelloUser> findById(String id);
}
