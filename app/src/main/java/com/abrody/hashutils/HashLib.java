package com.abrody.hashutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by andy on 10/22/15.
 */
public class HashLib {

    public static String sha1sum(String input) {
        // MessageDigest md = DigestUtils.getDigest('SHA1');
        // We can't use sha1Hex due to Android's stupid old version of commons-codec
        return new String(Hex.encodeHex(DigestUtils.sha1(input)));
    }

    public static final Set<String> supportedAlgorithms = new HashSet<String>(Arrays.asList(
            "MD5", "SHA1", "SHA256", "SHA512"));

    public boolean isSupportedAlgorithm(String algo) {
        return supportedAlgorithms.contains(algo);
    }

    public static String hexDigest(String algorithm, String input) {
        MessageDigest md = DigestUtils.getDigest(algorithm);
        return new String(Hex.encodeHex(md.digest(StringUtils.getBytesUtf8(input))));
    }

    public static String hexDigest(String algorithm, byte[] input) {
        MessageDigest md = DigestUtils.getDigest(algorithm);
        return new String(Hex.encodeHex(md.digest(input)));
    }

    public static String hexDigest(String algorithm, InputStream input) throws IOException {
        MessageDigest md = DigestUtils.getDigest(algorithm);
        return new String(Hex.encodeHex(digest(md, input)));
    }

    public static String hexDigest(String algorithm, File file) throws IOException {
        return hexDigest(algorithm, new FileInputStream(file));
    }


    private static final int STREAM_BUFFER_LENGTH = 4096;

    /**
     * Read through an InputStream and returns the digest for the data
     *
     * @param digest
     *            The MessageDigest to use (e.g. MD5)
     * @param data
     *            Data to digest
     * @return MD5 digest
     * @throws IOException
     *             On error reading from the stream
     */
    public static byte[] digest(MessageDigest digest, InputStream data) throws IOException {
        byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
        int read = data.read(buffer, 0, STREAM_BUFFER_LENGTH);

        while (read > -1) {
            digest.update(buffer, 0, read);
            read = data.read(buffer, 0, STREAM_BUFFER_LENGTH);
        }

        return digest.digest();
    }
}