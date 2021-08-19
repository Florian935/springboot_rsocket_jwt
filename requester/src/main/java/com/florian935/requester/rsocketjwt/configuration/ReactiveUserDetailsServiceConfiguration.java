package com.florian935.requester.rsocketjwt.configuration;

import com.florian935.requester.rsocketjwt.domain.HelloUser;
import com.florian935.requester.rsocketjwt.service.AuthenticationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Configuration
public class ReactiveUserDetailsServiceConfiguration {

    @Bean
    ReactiveUserDetailsService reactiveUserDetailsService(AuthenticationService authenticationService) {

        return username -> authenticationService
                .findByUsername(username)
                .map(this::buildUserDetails);
    }

    private UserDetails buildUserDetails(HelloUser user) {

        return User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(fromRolesListToGrantedAuthorityList(user.getRole()))
                .build();
    }

    private List<SimpleGrantedAuthority> fromRolesListToGrantedAuthorityList(String role) {

        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }
}
