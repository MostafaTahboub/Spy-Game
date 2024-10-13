package com.example.demo.security;

import com.example.demo.chatgpt.ChatRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final String secret = "mySecretKey";  // The secret key to sign the JWT

    private final Map<String, String> tokenCache = new ConcurrentHashMap<>();

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)  // The token will hold the username as the subject
                .setIssuedAt(new Date(System.currentTimeMillis()))  // Token creation date
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))  // Token expiration (10 hours)
                .signWith(SignatureAlgorithm.HS256, secret)  // Signing with the secret key and HS256 algorithm
                .compact();
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));  // Check if the token is valid and not expired
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);  // Extract the username (subject) from the token
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);  // Extract expiration date from the token
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);  // Extract all claims
        return claimsResolver.apply(claims);  // Apply the claims resolver
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();  // Parse the token using the secret key
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());  // Check if the token has expired
    }

    public String getTokenFromCache(String username) {
        return tokenCache.get(username);
    }

    public void storeTokenInCache(String username, String token) {
        tokenCache.put(username, token);
    }
}
