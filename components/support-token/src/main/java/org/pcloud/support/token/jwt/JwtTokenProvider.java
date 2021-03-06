package org.pcloud.support.token.jwt;

import io.jsonwebtoken.*;
import org.pcloud.support.token.core.DateProvider;
import org.pcloud.support.token.core.TokenProvider;
import org.pcloud.support.token.core.Token;
import org.pcloud.support.token.core.UuidProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider implements TokenProvider<JwtToken, JwtTokenGenerateRequest, JwtTokenInformation<Token>> {
    private final DateProvider dateProvider;
    private final UuidProvider uuidProvider;
    private final String secretKey;

    public JwtTokenProvider(DateProvider dateProvider, UuidProvider uuidProvider, @Value("${module.jwt.secret-key}") String secretKey) {
        this.dateProvider = dateProvider;
        this.uuidProvider = uuidProvider;
        this.secretKey = secretKey;
    }

    @Override
    public JwtToken generate(JwtTokenGenerateRequest request) {
        String requestId = uuidProvider.randomUUID().toString();
        Date date = dateProvider.now();

        String token = _generate(requestId, date, request.getValidityMS());
        String refresh = _generate(requestId, date, request.getRefreshValidityMS());

        return new JwtToken(token, refresh);
    }

    private String _generate(String requestId, Date date, long validityMS) {
        Claims claims = Jwts.claims().setSubject(requestId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + validityMS))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    @Override
    public JwtTokenInformation<Token> getInformation(String token) {
        Claims body = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

        return new JwtTokenInformation<>(new Token(token), body.getSubject(), body.getIssuedAt());
    }

    public boolean isExpiration(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException expiredJwtException) {
//            expiredJwtException.printStackTrace();
            return true;
        }
        return false;
    }
}
