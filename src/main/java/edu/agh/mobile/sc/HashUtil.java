package edu.agh.mobile.sc;

import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Przemyslaw Dadel
 */
public class HashUtil {

    private static final byte[] SALT = "09abb2788535a5d95410".getBytes();
    private static final MessageDigest sha256;

    static {
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public static synchronized String hash(String value) {
        sha256.update(SALT);
        return new String(Hex.encodeHex(sha256.digest(value.getBytes())));
    }

    public static String hash(int value) {
        return hash(Integer.toString(value));
    }

}
