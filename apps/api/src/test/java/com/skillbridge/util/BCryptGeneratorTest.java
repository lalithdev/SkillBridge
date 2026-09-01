package com.skillbridge.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.LinkedHashMap;
import java.util.Map;

public class BCryptGeneratorTest {

    @Test
    void testBCryptPasswordEncoding() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "TestPassword@123";
        String encodedHash = encoder.encode(rawPassword);

        org.junit.jupiter.api.Assertions.assertNotNull(encodedHash);
        org.junit.jupiter.api.Assertions.assertTrue(encoder.matches(rawPassword, encodedHash));
        org.junit.jupiter.api.Assertions.assertFalse(encoder.matches("WrongPassword", encodedHash));
    }
}
