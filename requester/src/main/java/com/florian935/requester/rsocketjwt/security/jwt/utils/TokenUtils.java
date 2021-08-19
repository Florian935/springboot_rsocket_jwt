package com.florian935.requester.rsocketjwt.security.jwt.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.florian935.requester.rsocketjwt.domain.HelloUser;
import com.florian935.requester.rsocketjwt.domain.UserToken;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Configuration
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TokenUtils {

    static long ACCESS_EXPIRE = 15;
    static String ACCESS_SECRET_KEY = "dfg39wLJ92kdI29084JJQjhsj98ksdfKSJnk91Kkjb87GGb898nbBbBBBbsdfkze2KFjksdfDNFSK";
    static Algorithm ACCESS_ALGORITHM = Algorithm.HMAC512(ACCESS_SECRET_KEY);
    static MacAlgorithm MAC_ALGORITHM = MacAlgorithm.HS512;
    static String HMAC_SHA_512 = "HmacSHA512";

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
                .withClaim("username", user.getUsername())
                .withExpiresAt(Date.from(instant))
                .sign(algorithm);

        return UserToken.builder().tokenId(tokenId).token(token).user(user).build();
    }

    public UserToken generateAccessToken(HelloUser user) {

        return generateToken(user, ACCESS_ALGORITHM, ACCESS_EXPIRE, ChronoUnit.MINUTES);
    }

    public boolean isValidToken(String token) {

        try {

            JWT.decode(token);

            return true;

        } catch (JWTDecodeException exception) {

            throw new JWTDecodeException(exception.getMessage());
        }
    }

    public Authentication getAuthenticationFromToken(String token) {

        final Collection<? extends GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_" + JWT.decode(token).getClaim("role").asString()));

        final User principal = new User(
                JWT.decode(token).getClaim("username").asString(),
                "",
                authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
}
