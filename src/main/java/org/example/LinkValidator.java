package org.example;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class LinkValidator {
    public static int getHttpStatus(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            return connection.getResponseCode();
        } catch (IOException e) {
            System.err.println("Error al verificar el enlace: " + link + " - " + e.getMessage());
            return -1;
        }
    }
}