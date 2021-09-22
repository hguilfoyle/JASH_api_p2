package com.revature.jash.web.util.security;

import java.lang.annotation.*;

//TODO: Make SecureWithSameId : Only person matching id can hit this endpoint

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Secured {
    String[] allowedUsers();
}