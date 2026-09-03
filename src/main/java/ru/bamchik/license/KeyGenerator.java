package ru.bamchik.license;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;

public class KeyGenerator {
    private static final String SALT = "BamchikClientSecretSalt2026";

    public static String generateKey(String username) {
        try {
            String raw = username + SALT + Instant.now().toString();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(raw.getBytes());
            String base64 = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
            String shortKey = base64.substring(0, 20).toUpperCase();
            return shortKey.replaceAll("(.{4})", "$1-").replaceFirst("-$", "");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static boolean verifyKey(String key, String username) {
        String generated = generateKey(username);
        return generated != null && generated.equals(key);
    }
}