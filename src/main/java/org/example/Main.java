package org.example;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingresa el URL:");
        String url = scanner.nextLine();

        try {
            LinkExtractor extractor = new LinkExtractor(url);
            FileManager fileManager = new FileManager();
            ContentDownloader contentDownloader = new ContentDownloader();

            fileManager.writeAllLinks(extractor);
            fileManager.writeValidLinks(extractor);

            contentDownloader.downloadContent(extractor);

            System.out.println("Datos guardados exitosamente en 'all_links.txt', 'valid_links.txt' y 'images_and_links.pdf'.");
        } catch (IOException e) {
            System.out.println("Hubo un error al procesar la URL.");
            e.printStackTrace();
        }
    }
}