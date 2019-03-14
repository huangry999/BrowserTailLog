package com.log.uiapi.security;

import com.log.common.printer.BytePrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class UserService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    @Value("${security.auth:''}")
    private String systemPassword;
    private final static String SALT = "34)8e$";

    @Autowired
    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        final MessageDigest messageDigest;
//        try {
//            messageDigest = MessageDigest.getInstance("SHA-256");
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
//        final String p = SALT + systemPassword;
//        messageDigest.update(p.getBytes());
//        String encrypt = BytePrinter.toString0(messageDigest.digest(), "");
//        System.out.println(encrypt);
        return User.builder()
                .username("default")
                .password(passwordEncoder.encode(systemPassword))
                .roles("admin")
                .build();
    }
}
