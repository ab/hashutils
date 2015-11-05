package com.abrody.hashutils;

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
}
