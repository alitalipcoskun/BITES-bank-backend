package com.example.banking_project.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


/*
    In order to manipulate the JWTTokens, we require another dependency.
    Firstly, we extracted All claims, then extracted single claim
    after that we added SECRET_KEY variable to implement getSignInKey method.

    Other methods will be implemented for checking the followings:
    Token is expired or not
    Extracting the expiration date
    Generating the token etc...
*/

@Slf4j
@Service
public class JwtService {
    //
    private static final String SECRET_KEY = "f83537a444dc555017d470bda7286a6cad0c9d781536a6578cd4d03d6305733b";
    public String extractUsername(String token) {
        //The subject is phone for my case.
        return extractClaim(token, Claims::getSubject);
    }

    //Implementing another method to allow developer to extract a single claim that is passed.
    public <T> T extractClaim(String token,
                              Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims,
                                UserDetails userDetails)
    {
        //Generates token
        return buildToken(extraClaims, userDetails);
    }

    public String buildToken(Map<String, Object> extraClaims, UserDetails userDetails){
        log.info("JWT successfully generated.");
        Date expirationDate = new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24));
        log.info("Token expiration date: " + expirationDate);
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername()) //We set our subject in here.
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expirationDate)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        //It will check whether token is owned by user or not
        log.info("JWT is getting checked...");
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token){
        /*
            SignIn key is a secret kay to be used in the signing process digitally in JWT.
            It is used to create the signature part of JWT.
            Signature part is used to verify the sender anc receiver, and the sender JWT did not change from 3rd party people
            or software.
            As a backend service the verification of JWT did not change during process is mandatory.

            The sign in key will be used in a conjuction with the sign-in algorithm specified in the JWT header to create the signature.
            The key size is depending on the level of the trust of the sign-in party and your application.
            There are tools to create SHA-256 or other signing approaches.
         */
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        log.info("Key generated:", keyBytes);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
