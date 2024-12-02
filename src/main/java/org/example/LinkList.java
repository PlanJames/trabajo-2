package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class LinkList {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingresa el URL:");
        String url = scanner.nextLine();

        try (
                BufferedWriter allLinksWriter = new BufferedWriter(new FileWriter("src/main/resources/all_links.txt"));
                BufferedWriter validLinksWriter = new BufferedWriter(new FileWriter("src/main/resources/valid_links.txt"))
        ) {
            Document doc = Jsoup.connect(url).get();
            System.out.println("Conexión exitosa");

            // Obtener los elementos (enlaces, imágenes y medios)
            Elements links = doc.select("a[href]");
            Elements media = doc.select("[src]");
            Elements imagenes = doc.select("img[src~=(?i)\\.(jpeg|jpg|png|gif|bmp|tiff|tif|svg|webp|avif|heif|ico)]");

            // Escribir todos los enlaces en el archivo all_links.txt
            allLinksWriter.write("Links:");
            allLinksWriter.newLine();
            processAndWriteAll(allLinksWriter, links, "abs:href");

            // Escribir todos los elementos multimedia en el archivo all_links.txt
            allLinksWriter.write("Media:");
            allLinksWriter.newLine();
            processAndWriteAll(allLinksWriter, media, "abs:src");

            // Escribir todas las imágenes en el archivo all_links.txt
            allLinksWriter.write("Imágenes:");
            allLinksWriter.newLine();
            processAndWriteAll(allLinksWriter, imagenes, "abs:src");

            // Escribir solo los enlaces válidos (código de estado 200) en valid_links.txt
            validLinksWriter.write("Enlaces Válidos (Estado 200):");
            validLinksWriter.newLine();
            filterAndWriteValidLinks(validLinksWriter, links, "abs:href");

            // Escribir solo los elementos multimedia válidos (estado 200) en valid_links.txt
            validLinksWriter.write("Media Válidos (Estado 200):");
            validLinksWriter.newLine();
            filterAndWriteValidLinks(validLinksWriter, media, "abs:src");

            // Escribir solo las imágenes válidas (estado 200) en valid_links.txt
            validLinksWriter.write("Imágenes Válidos (Estado 200):");
            validLinksWriter.newLine();
            filterAndWriteValidLinks(validLinksWriter, imagenes, "abs:src");

            System.out.println("Datos guardados exitosamente en 'all_links.txt' y 'valid_links.txt'.");

        } catch (IOException e) {
            System.out.println("Hubo un error al procesar la URL.");
            e.printStackTrace();
        }
    }

    // Método para procesar todos los enlaces y escribirlos en el archivo
    private static void processAndWriteAll(BufferedWriter writer, Elements elements, String attrKey) throws IOException {
        // Obtener todos los enlaces (sin filtrar) y escribir en el archivo
        List<String> allLinks = elements.stream()
                .map(element -> element.attr(attrKey)) // Extraer atributo
                .collect(Collectors.toList());

        // Escribir en el archivo
        for (String link : allLinks) {
            int status = getHttpStatus(link);
            writer.write(link); // Escribir enlace y su código de estado
            writer.newLine();
        }
        writer.write("---------------------------");
        writer.newLine();
    }

    // Método para filtrar los enlaces válidos (estado 200) y escribirlos en el archivo
    private static void filterAndWriteValidLinks(BufferedWriter writer, Elements elements, String attrKey) throws IOException {
        // Filtrar enlaces válidos (estado 200) usando Java Streams
        List<String> validLinks = elements.stream()
                .map(element -> element.attr(attrKey)) // Extraer atributo
                .filter(link -> getHttpStatus(link) == 200) // Filtrar solo los que tienen estado 200
                .collect(Collectors.toList());

        // Escribir en el archivo solo los enlaces válidos
        for (String link : validLinks) {
            writer.write(link); // Escribir enlace y su código de estado
            writer.newLine();
        }
        writer.write("---------------------------");
        writer.newLine();
    }

    // Método para obtener el estado HTTP
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