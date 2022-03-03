package org.pcloud.support.token.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.pcloud.support.token.core.DateProvider;
import org.pcloud.support.token.core.Token;
import org.pcloud.support.token.core.TokenProvider;
import org.pcloud.support.token.core.UuidProvider;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider implements TokenProvider<JwtToken, JwtTokenGenerateRequest, JwtTokenInformation<Token>> {
    private final DateProvider dateProvider;
    private final UuidProvider uuidProvider;
    private final String secretKey;

    @Override
    public JwtToken generate(JwtTokenGenerateRequest request) {
        Claims claims = Jwts.claims().setSubject(uuidProvider.randomUUID().toString());
        claims.put("role", request.getRole());
        claims.put("validity", request.getValidityMS());

        Date date = dateProvider.now();

        String token = _generate(claims, date, request.getValidityMS());
        String refresh = _generate(claims, date, request.getRefreshValidityMS());

        return new JwtToken(token, refresh);
    }

    private String _generate(Claims claims, Date date, long validityMS) {
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
        String role = body.get("role", String.class);
        Long validity = body.get("validity", Long.class);
        return new JwtTokenInformation<Token>(new Token(token), body.getSubject(), role, validity, body.getIssuedAt());
    }
}
