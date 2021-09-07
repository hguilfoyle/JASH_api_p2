package com.revature.jash.web.util.security;

import com.revature.jash.web.dtos.Principal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Component
public class TokenParser {

    private final Logger logger = LoggerFactory.getLogger(SecurityAspect.class);
    private final JwtConfig jwtConfig;

    @Autowired
    public TokenParser(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public Optional<Principal> parseToken(HttpServletRequest req) {

        try {

            String header = req.getHeader(jwtConfig.getHeader());

            System.out.println("Header value: " + header);

            if (header == null || !header.startsWith(jwtConfig.getPrefix())) {
                logger.warn("Request originates from an unauthenticated source.");
                return Optional.empty();
            }

            String token = header.replaceAll(jwtConfig.getPrefix(), "");

            Claims jwtClaims = Jwts.parser()
                    .setSigningKey(jwtConfig.getSigningKey())
                    .parseClaimsJws(token)
                    .getBody();

            return Optional.of(new Principal(jwtClaims));

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Optional.empty();
        }

    }
}
