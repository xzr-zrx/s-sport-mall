package cn.bugstack.security;

import cn.bugstack.domain.auth.adapter.port.ITokenPort;
import cn.bugstack.domain.auth.model.entity.UserAccountEntity;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtTokenService implements ITokenPort {
    private final Algorithm algorithm;
    private final long expireHours;

    public JwtTokenService(@Value("${security.jwt.secret}") String secret,
                           @Value("${security.jwt.expire-hours:24}") long expireHours) {
        if (secret == null || secret.length() < 32) throw new IllegalArgumentException("JWT_SECRET 至少需要 32 个字符");
        this.algorithm = Algorithm.HMAC256(secret);
        this.expireHours = expireHours;
    }

    @Override
    public String issueToken(UserAccountEntity user) {
        Instant now = Instant.now();
        return JWT.create().withSubject(String.valueOf(user.getId())).withClaim("username", user.getUsername())
                .withClaim("role", user.getRole()).withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plus(expireHours, ChronoUnit.HOURS))).sign(algorithm);
    }

    public DecodedJWT verify(String token) { return JWT.require(algorithm).build().verify(token); }
}
