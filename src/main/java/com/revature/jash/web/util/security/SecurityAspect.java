package com.revature.jash.web.util.security;

import com.revature.jash.util.exceptions.AuthenticationException;
import com.revature.jash.util.exceptions.AuthorizationException;
import com.revature.jash.web.dtos.Principal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Aspect
@Component
public class SecurityAspect {

    private final Logger logger = LoggerFactory.getLogger(SecurityAspect.class);

    private final JwtConfig jwtConfig;
    private final TokenParser parser;

    @Autowired
    public SecurityAspect(JwtConfig jwtConfig, TokenParser parser) {
        this.jwtConfig = jwtConfig;
        this.parser = parser;
    }

    @Around("@annotation(com.revature.jash.web.util.security.Secured)")
    public Object secureEndpoint(ProceedingJoinPoint pjp) throws Throwable {

        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        Secured securedAnno = method.getAnnotation(Secured.class);
        List<String> allowedUsers = Arrays.asList(securedAnno.allowedUsers());

        HttpServletRequest req = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        Principal principal = parser.parseToken(req).orElseThrow(() -> new AuthenticationException("Request originates from an unauthenticated source."));

        if (allowedUsers.size() > 0 && !allowedUsers.contains(principal.getUsername())) {
            throw new AuthorizationException("A forbidden request was made by: " + principal.getUsername());
        }

        return pjp.proceed();

    }

}

