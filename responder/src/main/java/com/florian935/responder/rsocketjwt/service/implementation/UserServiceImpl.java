package com.florian935.responder.rsocketjwt.service.implementation;

import com.florian935.responder.rsocketjwt.domain.HelloUser;
import com.florian935.responder.rsocketjwt.repository.UserRepository;
import com.florian935.responder.rsocketjwt.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    @Override
    public Mono<HelloUser> findById(String id) {

        return userRepository.findById(id);
    }
}
