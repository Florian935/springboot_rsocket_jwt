package com.florian935.requester.rsocketjwt.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.florian935.requester.rsocketjwt.domain.HelloUser;
import com.florian935.requester.rsocketjwt.domain.UserToken;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Configuration
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TokenUtils {

    static long ACCESS_EXPIRE = 15;
    static long REFRESH_EXPIRE = 7;
    static String ACCESS_SECRET_KEY = "and0X3Rva2VuLWJhc2VkX29wZW5hcGlfZm9yX3Jzb2NrZXRfYWNjZXNzX3Rva2Vu";
    static String REFRESH_SECRET_KEY = "and0X3Rva2VuLWJhc2VkX29wZW5hcGlfNF9yc29ja2V0X3JlZnJlc2hfdG9rZW4=";
    static Algorithm ACCESS_ALGORITHM = Algorithm.HMAC256(ACCESS_SECRET_KEY);
    static Algorithm REFRESH_ALGORITHM = Algorithm.HMAC256(REFRESH_SECRET_KEY);
    static MacAlgorithm MAC_ALGORITHM = MacAlgorithm.HS256;
    static String HMAC_SHA_256 = "HmacSHA256";

    public UserToken generateToken(HelloUser user,
                                   Algorithm algorithm,
                                   long expire,
                                   ChronoUnit unit) {

        final String tokenId = UUID.randomUUID().toString();
        final Instant instant;
        final Instant now = Instant.now();

        if (now.isSupported(unit)) {
            instant = now.plus(expire, unit);
        } else {
            log.error("unit param is not supported");
            return null;
        }

        String token = JWT.create()
                .withJWTId(tokenId)
                .withSubject(user.getUserId())
                .withClaim("scope", user.getRole())
                .withExpiresAt(Date.from(instant))
                .sign(algorithm);

        return UserToken.builder().tokenId(tokenId).token(token).user(user).build();
    }

    public UserToken generateAccessToken(HelloUser user) {

        return generateToken(user, ACCESS_ALGORITHM, ACCESS_EXPIRE, ChronoUnit.MINUTES);
    }

    public UserToken generateRefreshToken(HelloUser user) {

        return generateToken(user, REFRESH_ALGORITHM, REFRESH_EXPIRE, ChronoUnit.DAYS);
    }

    public ReactiveJwtDecoder jwtAccessTokenDecoder() {

        final SecretKeySpec secretKey = new SecretKeySpec(ACCESS_SECRET_KEY.getBytes(), HMAC_SHA_256);

        return NimbusReactiveJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MAC_ALGORITHM)
                .build();
    }
}
