package de.pho.descent.web.auth;

import de.pho.descent.web.model.User;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author pho
 */
public class SecurityTools {

    private static final int ITERATION_NUMBER = 1000;

    public static boolean checkPassword(String clearText, String hash) {
        if (clearText == null || hash == null || hash.length() < 30) {
            return false;
        }
        try {
            String salt = hash.substring(27) + "=";
            String digest = hash.substring(0, 27) + "=";

            byte[] bDigest = Base64.decodeBase64(digest);
            byte[] bSalt = Base64.decodeBase64(salt);

            byte[] proposedDigest = getHash(ITERATION_NUMBER, clearText, bSalt);

            return Arrays.equals(proposedDigest, bDigest);

        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String createHash(String password) {

        if (password == null) {
            throw new RuntimeException("Build a hash from null is not possible!");
        }
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

            // Salt generation 64 bits long
            byte[] bSalt = new byte[8];
            random.nextBytes(bSalt);
            // Digest computation
            byte[] bDigest = getHash(ITERATION_NUMBER, password, bSalt);
            String sDigest = Base64.encodeBase64String(bDigest);
            String sSalt = Base64.encodeBase64String(bSalt);

            // remove trailing '=' and line termination characters
            sDigest = sDigest.replaceAll("=\\s*$", "");
            sSalt = sSalt.replaceAll("=\\s*$", "");

            return sDigest + sSalt;

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String createRecoveryHash(User user) {
        String hash;
        // only letters and digits are allowed (because of the interpretation in
        // the url)
        String pattern = "[^a-zA-Z0-9]";
        Pattern p = Pattern.compile(pattern);
        Matcher matcher;
        do {
            hash = createHash(user.getUsername() + System.currentTimeMillis());
            matcher = p.matcher(hash);
        } while (matcher.find());
        return hash;
    }

    /**
     * From a password, a number of iterations and a salt, returns the
     * corresponding digest
     *
     * @param iterationNb int The number of iterations of the algorithm
     * @param password String The password to encrypt
     * @param salt byte[] The salt
     * @return byte[] The digested password
     * @throws NoSuchAlgorithmException If the algorithm doesn't exist
     * @throws UnsupportedEncodingException
     */
    private static byte[] getHash(int iterationNb, String password, byte[] salt)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        digest.update(salt);
        byte[] input = digest.digest(password.getBytes("UTF-8"));
        for (int i = 0; i < iterationNb; i++) {
            digest.reset();
            input = digest.digest(input);
        }
        return input;
    }
}
