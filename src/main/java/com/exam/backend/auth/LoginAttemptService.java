package com.exam.backend.auth;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {

    private final Map<String, AttemptState> attempts = new ConcurrentHashMap<>();
    private final int maxFailedAttempts;
    private final long lockDurationMinutes;

    public LoginAttemptService(
            @Value("${app.auth.max-failed-attempts:5}") int maxFailedAttempts,
            @Value("${app.auth.lock-duration-minutes:15}") long lockDurationMinutes) {
        this.maxFailedAttempts = maxFailedAttempts;
        this.lockDurationMinutes = lockDurationMinutes;
    }

    public boolean isBlocked(String username) {
        AttemptState state = attempts.get(username);
        if (state == null || state.lockedUntil == null) {
            return false;
        }

        if (state.lockedUntil.isBefore(Instant.now())) {
            attempts.remove(username);
            return false;
        }

        return true;
    }

    public long remainingLockSeconds(String username) {
        AttemptState state = attempts.get(username);
        if (state == null || state.lockedUntil == null) {
            return 0;
        }

        long remaining = state.lockedUntil.getEpochSecond() - Instant.now().getEpochSecond();
        return Math.max(remaining, 0);
    }

    public void recordSuccess(String username) {
        attempts.remove(username);
    }

    public void recordFailure(String username) {
        AttemptState state = attempts.computeIfAbsent(username, key -> new AttemptState());
        state.failedCount++;
        if (state.failedCount >= maxFailedAttempts) {
            state.lockedUntil = Instant.now().plusSeconds(lockDurationMinutes * 60);
            state.failedCount = 0;
        }
    }

    private static class AttemptState {
        private int failedCount;
        private Instant lockedUntil;
    }
}
