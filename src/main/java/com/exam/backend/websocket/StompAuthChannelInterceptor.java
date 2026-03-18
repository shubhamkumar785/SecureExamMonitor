package com.exam.backend.websocket;

import com.exam.backend.auth.JwtService;
import com.exam.backend.auth.TokenDenylistService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenDenylistService tokenDenylistService;

    public StompAuthChannelInterceptor(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            TokenDenylistService tokenDenylistService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.tokenDenylistService = tokenDenylistService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Missing Authorization header for websocket connection");
            }

            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            if (tokenDenylistService.isRevoked(jwtService.extractTokenId(token))) {
                throw new IllegalArgumentException("JWT token has been revoked");
            }
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (!jwtService.isTokenValid(token, userDetails)) {
                throw new IllegalArgumentException("Invalid JWT token for websocket connection");
            }

            var authorities = userDetails.getAuthorities();
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    authorities);
            accessor.setUser(authentication);
        }

        return message;
    }
}
