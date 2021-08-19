package com.florian935.requester.rsocketjwt.repository;

import com.florian935.requester.rsocketjwt.domain.HelloUser;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<HelloUser, String> {

    Mono<HelloUser> findByUsername(String userName);
}
