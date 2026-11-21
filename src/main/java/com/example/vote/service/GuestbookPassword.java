package com.example.vote.service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 방명록 삭제용 비밀번호를 해시로 저장하고 확인함.
 * 6개 서비스(binaryworld·game·vote·food·quiz·face)가 같은 guestbook 테이블을 쓰니 형식을 똑같이 맞춰야 함
 */
public final class GuestbookPassword {

    private static final String PREFIX = "pbkdf2$";
    private static final int ITERATIONS = 120000;
    private static final SecureRandom RANDOM = new SecureRandom();

    private GuestbookPassword() {
    }

    // 평문 → pbkdf2$반복횟수$소금$해시
    public static String hash(String raw) {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        byte[] hash = derive(raw == null ? "" : raw, salt, ITERATIONS);
        return PREFIX + ITERATIONS + "$" + Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean isHashed(String stored) {
        return stored != null && stored.startsWith(PREFIX);
    }

    // 예전에 평문으로 저장된 글도 지울 수 있게 둘 다 확인함
    public static boolean matches(String raw, String stored) {
        if (raw == null || stored == null) return false;
        if (!isHashed(stored)) {
            return MessageDigest.isEqual(raw.getBytes(StandardCharsets.UTF_8), stored.getBytes(StandardCharsets.UTF_8));
        }
        try {
            String[] parts = stored.split("\\$");
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            return MessageDigest.isEqual(expected, derive(raw, salt, iterations));
        } catch (RuntimeException e) {
            return false;
        }
    }

    private static byte[] derive(String raw, byte[] salt, int iterations) {
        try {
            PBEKeySpec spec = new PBEKeySpec(raw.toCharArray(), salt, iterations, 256);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("비밀번호 해시 실패", e);
        }
    }
}
