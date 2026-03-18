package com.exam.backend.auth;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenDenylistService {

    private final Map<String, Instant> revokedTokenIds = new ConcurrentHashMap<>();

    public void revoke(String token, JwtService jwtService) {
        String tokenId = jwtService.extractTokenId(token);
        Date expiresAt = jwtService.extractExpiration(token);
        if (tokenId == null || tokenId.isBlank() || expiresAt == null) {
            return;
        }
        revokedTokenIds.put(tokenId, expiresAt.toInstant());
    }

    public boolean isRevoked(String tokenId) {
        if (tokenId == null || tokenId.isBlank()) {
            return true;
        }

        Instant expiry = revokedTokenIds.get(tokenId);
        if (expiry == null) {
            return false;
        }

        if (expiry.isBefore(Instant.now())) {
            revokedTokenIds.remove(tokenId);
            return false;
        }

        return true;
    }
}
