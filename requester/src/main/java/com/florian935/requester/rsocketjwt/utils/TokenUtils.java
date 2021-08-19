package com.florian935.requester.rsocketjwt.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.florian935.requester.rsocketjwt.domain.HelloUser;
import com.florian935.requester.rsocketjwt.domain.UserToken;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
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
    static String ACCESS_SECRET_KEY = "dfg39wLJ92kdI29084JJQjhsj98ksdfKSJnk91Kkjb87GGb898nbBbBBBbsdfkze2KFjksdfDNFSK";
    static Algorithm ACCESS_ALGORITHM = Algorithm.HMAC512(ACCESS_SECRET_KEY);

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
}
