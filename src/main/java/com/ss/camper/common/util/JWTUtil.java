package com.ss.camper.common.util;

import com.ss.camper.oauth2.config.AuthProperties;
import com.ss.camper.oauth2.dto.UserPrincipal;
import com.ss.camper.oauth2.exception.ExpiredTokenException;
import com.ss.camper.oauth2.exception.NotValidTokenException;
import com.ss.camper.oauth2.exception.UnsupportedTokenException;
import com.ss.camper.user.application.dto.UserInfoDTO;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JWTUtil {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final AuthProperties AuthProperties;

    public String creatAuthToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return creatToken(userPrincipal.getName(), userPrincipal.getUsername(), AuthProperties.getToken().getAuthTokenExpirationTime());
    }

    public String creatFindPasswordToken(final String userId, final String email) {
        return creatToken(userId, email, AuthProperties.getToken().getFindPasswordTokenExpirationTime());
    }

    private String creatToken(final String userId, final String email, final long expiryTime) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiryTime);

        Map<String, Object> payloads = new HashMap<>();
        payloads.put(Claims.SUBJECT, userId);
        payloads.put("username", email);

        return Jwts.builder()
                .setId(userId)
                .setSubject(email)
                .setClaims(payloads)
                .setHeaderParam("typ", "JWT")
                .setIssuer("Camper")
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, AuthProperties.getToken().getTokenSecret())
                .compact();
    }

    public Claims getBody(String token) {
        this.validateToken(token);
        return Jwts.parser()
                .setSigningKey(AuthProperties.getToken().getTokenSecret())
                .parseClaimsJws(token)
                .getBody();

    }

    public UserPrincipal getPrincipal(String token) {
        Claims claims = getBody(token);
        return UserPrincipal.create(UserInfoDTO.builder()
                .id(Long.parseLong(claims.getSubject()))
                .email(String.valueOf(claims.get("username")))
                .build());
    }

    public Date getExpiredDate(String token) {
        return this.getBody(token).getExpiration();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(AuthProperties.getToken().getTokenSecret()).parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException | IllegalArgumentException e) { // 유효하지 않은 JWT 서명 | 유효하지 않은 JWT | 빈값
            throw new NotValidTokenException();
        } catch (ExpiredJwtException e) { // 만료된 JWT
            throw new ExpiredTokenException();
        } catch (UnsupportedJwtException e) { // 지원하지 않는 JWT
            throw new UnsupportedTokenException();
        }
    }

}