package de.pho.descent.shared.auth;

import de.pho.descent.shared.model.Player;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author pho
 */
public class SecurityTools {

    private static final Logger LOG = Logger.getLogger(SecurityTools.class.getName());
    private static final int ITERATION_NUMBER = 1000;
    private static final String PWD_SECRET = "CgM0MTgSVUNlbG9uaXMgUGFydG5lciBMa"
            + "WNlbnNlIGZvciBFbm93YSAtIFNpbmdsZSBVc2VyIERlbW8gTGljZW5zZTogTm90I"
            + "5xUYIZvpvEP7KggDqIJLvhxsoe6gWPnkVzfZtBFc+7BoBBaFyVXvj29EPr7XesPB";

    public static boolean checkAuthenticationDigest(String passwordHash, String restMethod, String restURI, String authDigest) {
        StringBuilder sb = new StringBuilder(passwordHash);
        sb.append("+").append(restMethod);
        sb.append("+").append(restURI);

        return checkPassword(sb.toString(), authDigest);
    }

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

    public static String createAuthHeaderValue(String username, String password, String restMethod, String restURI) {
        String pwdHash = SecurityTools.createHash(password, false);
        String authDigest = createAuthenticationDigest(pwdHash, restMethod, restURI);
        String combined = username + ":" + authDigest;

        return Base64.encodeBase64String(combined.getBytes());
    }

    public static String[] extractDataFromAuthHeaderValue(String authToken) {
        String combined = new String(Base64.decodeBase64(authToken));

        return combined.split(":");
    }

    private static String createAuthenticationDigest(String passwordHash, String restMethod, String restURI) {
        StringBuilder sb = new StringBuilder(passwordHash);
        sb.append("+").append(restMethod);
        sb.append("+").append(restURI);

        return createHash(sb.toString(), true);
    }

    public static String createHash(String password, boolean randomSecret) {

        if (password == null) {
            throw new RuntimeException("Build a hash from null is not possible!");
        }
        try {
            // Salt generation 64 bits long
            byte[] bSalt = new byte[8];
            if (randomSecret) {
                SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
                random.nextBytes(bSalt);
            } else {
                bSalt = Base64.decodeBase64(PWD_SECRET);
            }

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

    public static String createRecoveryHash(Player user) {
        String hash;
        // only letters and digits are allowed (because of the interpretation in
        // the url)
        String pattern = "[^a-zA-Z0-9]";
        Pattern p = Pattern.compile(pattern);
        Matcher matcher;
        do {
            hash = createHash(user.getUsername() + System.currentTimeMillis(), true);
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
