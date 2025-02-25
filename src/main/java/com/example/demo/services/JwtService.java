package com.example.demo.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.lang.Objects;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private static final String SECRET_KEY ="ec5b3a41f66f6e9aa2ea0ff86f2317313f1672d9d28c24c1a87168209f01787c";

    public String generateToken(
            Map<String, Objects> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts
                .builder().claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInKey())
                .compact();
    }







    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }



    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
