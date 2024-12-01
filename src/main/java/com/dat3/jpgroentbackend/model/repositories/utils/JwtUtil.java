package com.dat3.jpgroentbackend.model.repositories.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

/**
 * Utility-klasse til håndtering af JWT (JSON Web Token).
 * Indeholder metoder til at generere, validere og udtrække data fra tokens.
 */
@Component
public class JwtUtil {
    // Nøgle brugt til at signere og verificere tokens.
    private final String SECRET_KEY = "test";

    /**
     *  Finder username fra et token
     *
     * @param token JWT
     * @return Brugernavnet (subject) fra token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Udtrækker udløbsdatoen fra en token
     *
     * @param token JWT
     * @return Udløbsdatoen for tokenet
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * udtrækker et specifikt claim fra en token
     *
     * @param token JWT
     * @param claimsResolver Funktion, der specifere hvilken claim der skal udtrækkes
     * @param <T> typen af claim der udtrækkes
     * @return værdien af den øsnkede claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Udtrækker alle claims fra et token.
     *
     * @param token JWT-token.
     * @return Et Claims-objekt, der indeholder alle claims i tokenet.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody() ;
    }

    /**
     * Genererer et token baseret på et brugernavn.
     *
     * @param subject Brugernavn (eller en anden identifikator) til token.
     * @return Et nyt JWT-token, der er gyldigt i 24 timer.
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
     * Validerer et token mod et brugernavn.
     *
     * @param token    JWT-token.
     * @param username Brugernavnet der skal sammenlignes med tokenets subject.
     * @return True hvis tokenet er gyldigt og tilhører brugeren, ellers False.
     */
    public Boolean validateToken(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return tokenUsername.equals(username);
    }

    /**
     * Generere en token
     * @param username brugernavnet der skal laves en token med
     * @return den nye token
     */
    public String generateToken(String username) {
        return createToken(username);
    }

}
