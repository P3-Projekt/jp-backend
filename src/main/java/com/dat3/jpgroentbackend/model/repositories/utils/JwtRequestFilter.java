package com.dat3.jpgroentbackend.model.repositories.utils;

import com.dat3.jpgroentbackend.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Filter to process JWT tokens for authentication in each request.
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil; // Utility class for JWT operations

    @Autowired
    CustomUserDetailsService customUserDetailsService; // Loads user details for authentication

    /**
     * Processes each request to authenticate the user based on the JWT token.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        System.out.println("Request Method: " + request.getMethod()); // Log request method for debugging

        // Allow OPTIONS requests without further processing
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK); // Returner status 200 for OPTIONS-anmodninger
            return;
        }

        // Extract Authorization header and parse JWT
        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // Extract token from the header
            try {
                username = jwtUtil.extractUsername(jwt); // Extract username from JWT
            } catch (ExpiredJwtException e) {
                System.out.println("JWT token expired");
            }
        }

        // Authenticate user if token is valid and user is not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username); // Load user details

            // Validate JWT token
            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(userDetails);
                SecurityContextHolder.getContext().setAuthentication(authentication); // Set authentication in context
            }
        }

        // Continue filter chain
        chain.doFilter(request, response);
    }

}
