package ru.bamchik.license;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class LicenseManager {
    public enum Mode { LOCAL, REMOTE }
    private static Mode mode = Mode.LOCAL;
    private static final String REMOTE_URL = "http://your-server.com/verify.php";

    public static boolean checkLicense(String key) {
        if (mode == Mode.LOCAL) return checkLocal(key);
        else return checkRemote(key);
    }

    private static boolean checkLocal(String key) {
        try {
            File file = new File("keys.txt");
            if (!file.exists()) return false;
            Set<String> valid = new HashSet<>();
            Files.lines(Paths.get(file.getPath())).forEach(line -> valid.add(line.trim()));
            return valid.contains(key);
        } catch (IOException e) { return false; }
    }

    private static boolean checkRemote(String key) {
        try {
            URL url = new URL(REMOTE_URL + "?key=" + key);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response = in.readLine();
                in.close();
                return "true".equalsIgnoreCase(response);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}