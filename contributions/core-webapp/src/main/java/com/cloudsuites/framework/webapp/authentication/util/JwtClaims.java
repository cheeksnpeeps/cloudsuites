package com.cloudsuites.framework.webapp.authentication.util;

import java.util.HashMap;
import java.util.Map;

public class JwtClaims {

    private Map<String, Object> claims = new HashMap<>();

    public void addClaim(String key, Object value) {
        claims.put(key, value);
    }

    public Object getClaim(String key) {
        return claims.get(key);
    }

    public Map<String, Object> getClaims() {
        return claims;
    }

    public void setClaims(Map<String, Object> claims) {
        this.claims = claims;
    }

    public void removeClaim(String key) {
        claims.remove(key);
    }

    public void clearClaims() {
        claims.clear();
    }
}
