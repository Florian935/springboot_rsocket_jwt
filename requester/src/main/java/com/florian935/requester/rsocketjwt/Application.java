package com.florian935.requester.rsocketjwt;

import com.florian935.requester.rsocketjwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static lombok.AccessLevel.PRIVATE;

@SpringBootApplication
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class Application {

    PasswordEncoder passwordEncoder;
    UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

//    @EventListener(ApplicationReadyEvent.class)
//    public void init() {
//
//        final HelloUser helloUser = HelloUser.builder()
//                .userId(UUID.randomUUID().toString())
//                .username("admin")
//                .password(passwordEncoder.encode("pass"))
//                .role("ADMIN")
//                .build();
//
//        userRepository.save(helloUser).subscribe();
//    }
}
