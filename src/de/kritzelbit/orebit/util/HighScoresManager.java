package de.kritzelbit.orebit.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HighScoresManager {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String seed = "yeahiknowyoucouldfindthisoutbutpleasedontbeanassholeok1234";
    private static final String addScoreURL = "http://kritzelbit.de/leaderboard/orbitale/AddScore.php?";
    private static final String topScoresURL = "http://kritzelbit.de/leaderboard/orbitale/TopScores.php";

    public static String fileCheckSum(String path) {
        String checksum = null;
        try {
            FileInputStream fis = new FileInputStream(path);
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] buffer = new byte[8192];
            int numOfBytesRead;
            while ((numOfBytesRead = fis.read(buffer)) > 0) {
                md.update(buffer, 0, numOfBytesRead);
            }
            byte[] hash = md.digest();
            checksum = toHex(hash);
        } catch (Exception ex) {}
        return checksum;
    }

    private static String stringCheckSum(String string) {
        try {
            byte[] bytesOfMessage = string.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(bytesOfMessage);
            return toHex(messageDigest);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(HighScoresManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(HighScoresManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public static List<String[]> getTopScores() {
        List<String[]> scores = new ArrayList<String[]>();
        StringBuilder sb = new StringBuilder();
        
        try {
            URL url = new URL(topScoresURL);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(url.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException ex) {
            Logger.getLogger(HighScoresManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HighScoresManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for (String s : sb.toString().split("<br>")){
            scores.add(new String[]{s.split("\t")[0],
                Integer.parseInt(s.split("\t")[1])+""});
        }
        return scores;
    }

    public static void submitScore(String name, int score) {
        try {
            String url = addScoreURL + "name=" + URLEncoder.encode(name, "UTF-8")
                    + "&score=" + score + "&hash=" + stringCheckSum(name + score + seed);
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            System.out.println("[HS]\tsubmitted highscore: " + name
                    + "(" + score + ") RESPONSE: " + con.getResponseCode());
        } catch (ProtocolException ex) {
            Logger.getLogger(HighScoresManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(HighScoresManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(HighScoresManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HighScoresManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "x", bi);
    }
    
}
