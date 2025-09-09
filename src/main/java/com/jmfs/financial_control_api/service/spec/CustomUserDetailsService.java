package com.jmfs.financial_control_api.service.spec;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface CustomUserDetailsService extends UserDetailsService{

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;
}
