package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class LinkList {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingresa el URL:");
        String url = scanner.nextLine();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/links.txt"))) {
            Document doc = Jsoup.connect(url).get();
            System.out.println("Conexión exitosa");

            Elements links = doc.select("a[href]");
            Elements media = doc.select("[src]");
            Elements imagenes = doc.select("img[src~=(?i)\\.(jpeg|jpg|png|gif|bmp|tiff|tif|svg|webp|avif|heif|ico)]");

            // Escribir los links
            writer.write("Links:");
            writer.newLine();
            for (int i = 0; i < links.size(); i++) {
                String link = links.get(i).attr("abs:href"); // Usar enlace absoluto
                int status = getHttpStatus(link);
                writer.write(link + " - Código de estado: " + status);
                writer.newLine();
                writer.write("---------------------------");
                writer.newLine();
            }

            writer.newLine();

            // Escribir el contenido multimedia
            writer.write("Media:");
            writer.newLine();
            for (int i = 0; i < media.size(); i++) {
                String mediaLink = media.get(i).attr("abs:src");
                int status = getHttpStatus(mediaLink);
                writer.write(mediaLink + " - Código de estado: " + status);
                writer.newLine();
                writer.write("---------------------------");
                writer.newLine();
            }

            writer.newLine();

            // Escribir las imágenes
            writer.write("Imágenes:");
            writer.newLine();
            for (int i = 0; i < imagenes.size(); i++) {
                String imageLink = imagenes.get(i).attr("abs:src");
                int status = getHttpStatus(imageLink);
                writer.write(imageLink + " - Código de estado: " + status);
                writer.newLine();
            }
            writer.write("---------------------------");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static int getHttpStatus(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000); // Tiempo de espera para conexión
            connection.setReadTimeout(5000);    // Tiempo de espera para lectura
            connection.connect();
            return connection.getResponseCode();
        } catch (IOException e) {
            System.err.println("Error al verificar el enlace: " + link + " - " + e.getMessage());
            return -1; // Código para indicar error en la conexión
        }
    }
}
