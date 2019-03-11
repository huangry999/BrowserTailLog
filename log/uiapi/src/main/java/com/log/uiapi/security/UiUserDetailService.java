package com.log.uiapi.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UiUserDetailService implements UserDetailsService {
    @Value("${security.auth:''}")
    private String systemPassword;

    @Override
    public UiUser loadUserByUsername(String username) throws UsernameNotFoundException {
        return new UiUser(null, systemPassword, new ArrayList<>());
    }
}

