package com.florian935.responder.rsocketjwt.repository;

import com.florian935.responder.rsocketjwt.domain.HelloUser;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends ReactiveMongoRepository<HelloUser, String> {


}
