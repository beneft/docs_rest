package com.project.util;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component("sec")
public class SecurityUtil {
    public boolean isOwner(Jwt jwt, String userId) {
        return jwt != null && jwt.getSubject().equals(userId);
    }
}
