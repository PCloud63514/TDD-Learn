package org.pcloud.support.token.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.pcloud.support.token.core.DateProvider;
import org.pcloud.support.token.core.TokenProvider;
import org.pcloud.support.token.core.UuidProvider;
import org.springframework.stereotype.Component;

import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider implements TokenProvider<JwtToken, JwtTokenGenerateRequest, JwtTokenInformation<JwtToken>> {
    private final DateProvider dateProvider;
    private final UuidProvider uuidProvider;
    private final String secretKey;

    @Override
    public JwtToken generate(JwtTokenGenerateRequest request) {
        Claims claims = Jwts.claims().setSubject(uuidProvider.randomUUID().toString());
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
    public JwtTokenInformation getInformation(String token) {
        return null;
    }
}
