package de.kritzelbit.orebit.util;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class CheckSum {

    public static String checkSum(String path) {
        String checksum = null;
        try {
            FileInputStream fis = new FileInputStream(path);
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] buffer = new byte[8192];
            int numOfBytesRead;
            while ((numOfBytesRead = fis.read(buffer)) > 0){
                md.update(buffer, 0, numOfBytesRead);
            }
            byte[] hash = md.digest();
            checksum = new BigInteger(1, hash).toString(16);
        } catch (Exception ex) {}
        return checksum;
    }
    
}
