package com.dat3.jpgroentbackend.model.repositories.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

// Utility class for handling JWT (JSON Web Token)
// operations such as generating, validating, and extracting data.
@Component
public class JwtUtil {
    private final String SECRET_KEY = "test"; // Secret key for signing and validating tokens

    /**
     * Extracts the username (subject) from a token.
     * @param token JWT token.
     * @return Username from the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from a token.
     * @param token JWT token.
     * @param claimsResolver Function to specify which claim to extract.
     * @param <T> Type of the claim.
     * @return Value of the requested claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from a token.
     * @param token JWT token.
     * @return Claims object containing all claims.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody() ;
    }

    /**
     * Generates a JWT token for a username, valid for 24 hours.
     * @param subject Username or identifier.
     * @return New JWT token.
     */
    private String createToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // Gyldig i 24 timer
                .signWith(
                        SignatureAlgorithm.HS256,SECRET_KEY
                )
                .compact();
    }

    /**
     * Validates a token against a username.
     * @param token JWT token.
     * @param username Username to compare with token's subject.
     * @return True if valid, otherwise False.
     */
    public Boolean validateToken(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return tokenUsername.equals(username);
    }

    /**
     * Generates a token for a given username.
     * @param username Username to associate with the token.
     * @return Generated token.
     */
    public String generateToken(String username) {
        return createToken(username);
    }

}
