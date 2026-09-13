package com.onlineexam.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Salted password hashing using PBKDF2 (PBKDF2WithHmacSHA256) from the JDK — no
 * third-party dependency.
 *
 * <p>Stored format is a single string: {@code iterations:base64(salt):base64(hash)}.
 * The salt makes identical passwords hash differently (defeats rainbow tables);
 * the high iteration count makes brute-forcing slow. Verification uses a
 * constant-time comparison to avoid timing side-channels.</p>
 */
public final class PasswordUtil {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH_BITS = 256; // => 32-byte derived key
    private static final int SALT_BYTES = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
        // utility class — no instances
    }

    /**
     * Hash a plaintext password for storage in {@code users.password_hash}.
     *
     * @param plain the plaintext password (never stored anywhere)
     * @return an encoded {@code iterations:salt:hash} string
     */
    public static String hash(String plain) {
        if (plain == null) {
            throw new IllegalArgumentException("password must not be null");
        }
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        byte[] derived = pbkdf2(plain.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS);
        return ITERATIONS
                + ":" + Base64.getEncoder().encodeToString(salt)
                + ":" + Base64.getEncoder().encodeToString(derived);
    }

    /**
     * Verify a plaintext password against a previously stored encoded hash.
     *
     * @param plain  the candidate plaintext password
     * @param stored the encoded {@code iterations:salt:hash} string from the DB
     * @return true only if the password matches
     */
    public static boolean verify(String plain, String stored) {
        if (plain == null || stored == null) {
            return false;
        }
        String[] parts = stored.split(":");
        if (parts.length != 3) {
            return false;
        }
        try {
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] expected = Base64.getDecoder().decode(parts[2]);
            byte[] actual = pbkdf2(plain.toCharArray(), salt, iterations, expected.length * 8);
            // constant-time comparison
            return MessageDigest.isEqual(expected, actual);
        } catch (RuntimeException e) {
            // malformed stored value, bad Base64, etc. — treat as "no match"
            return false;
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLengthBits) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLengthBits);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("PBKDF2 hashing failed", e);
        } finally {
            spec.clearPassword();
        }
    }
}
