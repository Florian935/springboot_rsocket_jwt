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

    static String ACCESS_SECRET_KEY = "dfg39wLJ92kdI29084JJQjhsj98ksdfKSJnk91Kkjb87GGb898nbBbBBBbsdfkze2KFjksdfDNFSK";
    static MacAlgorithm MAC_ALGORITHM = MacAlgorithm.HS512;
    static String HMAC_SHA_512 = "HmacSHA512";

    public ReactiveJwtDecoder jwtAccessTokenDecoder() {

        final SecretKeySpec secretKey = new SecretKeySpec(ACCESS_SECRET_KEY.getBytes(), HMAC_SHA_512);

        return NimbusReactiveJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MAC_ALGORITHM)
                .build();
    }
}
