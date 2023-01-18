package com.springboot.blog.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // inject dependencies 
    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // get JWT (token) from http request
        String token = getJWTfromRequest(request);

        // validate token
        if(org.springframework.util.StringUtils.hasText(token)
            && tokenProvider.validateToken(token)) {
            
            // get username from token
            String userName = tokenProvider.getUserNameFromJWT(token);

            // load user associated with token
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(userName);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
                
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // set spring security
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);

    }

    // Bear <accessToken>
    private String getJWTfromRequest(HttpServletRequest request) {
        String bearToken = request.getHeader("Authorization");
        if(org.springframework.util.StringUtils.hasText(bearToken)) {
            return bearToken.substring(7,bearToken.length());
        }
        return null;
    }
    
}
