package com.florian935.responder.rsocketjwt.utils;

import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

import javax.crypto.spec.SecretKeySpec;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Configuration
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TokenUtils {

    static String ACCESS_SECRET_KEY = "and0X3Rva2VuLWJhc2VkX29wZW5hcGlfZm9yX3Jzb2NrZXRfYWNjZXNzX3Rva2Vu";
    static MacAlgorithm MAC_ALGORITHM = MacAlgorithm.HS256;
    static String HMAC_SHA_256 = "HmacSHA256";

    public ReactiveJwtDecoder jwtAccessTokenDecoder() {

        final SecretKeySpec secretKey = new SecretKeySpec(ACCESS_SECRET_KEY.getBytes(), HMAC_SHA_256);

        return NimbusReactiveJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MAC_ALGORITHM)
                .build();
    }
}
