package com.jmfs.financial_control_api.config.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jmfs.financial_control_api.exceptions.UserNotFoundException;
import com.jmfs.financial_control_api.repository.UserRepository;
import com.jmfs.financial_control_api.service.spec.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter{
    private TokenService tokenService;
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException
    {
        log.debug("[SECURITY FILTER] Filtering request for token");
        var token = this.recoverToken(request);
        if (token != null){
            log.debug("[SECURITY FILTER] Validating token");
            var subject = tokenService.validateToken(token);
            CustomUserDetails user = new CustomUserDetails(userRepository.findByEmail(subject).orElseThrow(() -> new UserNotFoundException("User not found")));
            var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            log.debug("[SECURITY FILTER] Valid token founded for user {}", user.getUsername());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }else
            filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if(authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}
