package nl.fontys.kassasysteem.kassa_systeem_backend.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.List;

@Component
public class JwtTokenUtil {

    private final JwtProperties jwtProperties;

    public JwtTokenUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtProperties.getSecret().getBytes())
                .build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername()) && !isExpired(token);
    }

    private boolean isExpired(String token) {
        Date exp = Jwts.parserBuilder()
                .setSigningKey(jwtProperties.getSecret().getBytes())
                .build().parseClaimsJws(token).getBody().getExpiration();
        return exp.before(new Date());
    }

    private String createTestToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", List.of(role)) // deze claim gebruik je misschien via @PreAuthorize
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(Keys.hmacShaKeyFor("DitIsEenGoedeTestSleutelVoorJWTMetMeerDan32Tekens!".getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

}

