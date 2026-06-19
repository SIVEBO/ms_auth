package com.sivebo.ms_auth.utils;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

        @Value("${jwt.secret}")
        private String secret;

        @Value("${jwt.expiration-ms}")
        private long expirationMs;

        private SecretKey getSigningKey() {
                byte[] keyBytes = Decoders.BASE64.decode(secret);
                return Keys.hmacShaKeyFor(keyBytes);
        }

        public String generateToken(String username, String rol) {
                Date now = new Date();
                Date expiration = new Date(now.getTime() + expirationMs);

                return Jwts.builder()
                                .subject(username)
                                .claim("rol", rol)
                                .issuedAt(now)
                                .expiration(expiration)
                                .signWith(getSigningKey())
                                .compact();
        }

        public String extractUsername(String token) {
                return extractClaim(token, Claims::getSubject);
        }

        public String extractRol(String token) {
                return extractClaim(token, claims -> claims.get("rol", String.class));
        }

        public Date extractExpiration(String token) {
                return extractClaim(token, Claims::getExpiration);
        }

        public boolean isTokenValid(String token) {
                try {
                        return !isTokenExpired(token);
                } catch (Exception e) {
                        return false;
                }
        }

        private boolean isTokenExpired(String token) {
                return extractExpiration(token).before(new Date());
        }

        private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
                Claims claims = Jwts.parser()
                                .verifyWith(getSigningKey())
                                .build()
                                .parseSignedClaims(token)
                                .getPayload();
                return claimsResolver.apply(claims);
        }
}
