package com.abrody.hashutils;

import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;
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
}
