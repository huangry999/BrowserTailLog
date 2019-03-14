package com.log;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Test {
    public static void main(String[] args) throws Exception {
        System.out.println(new BCryptPasswordEncoder().encode("clientpassword"));
    }
}
