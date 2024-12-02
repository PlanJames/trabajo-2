package org.example;

import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {
    public void writeAllLinks(LinkExtractor extractor) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/all_links.txt"))) {
            writeElements(writer, "Links:", extractor.getLinks(), "abs:href");
            writeElements(writer, "Media:", extractor.getMedia(), "abs:src");
            writeElements(writer, "Imágenes:", extractor.getImages(), "abs:src");
        }
    }

    public void writeValidLinks(LinkExtractor extractor) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/valid_links.txt"))) {
            writeValidElements(writer, "Enlaces Válidos:", extractor.getLinks(), "abs:href");
            writeValidElements(writer, "Media Válidos:", extractor.getMedia(), "abs:src");
            writeValidElements(writer, "Imágenes Válidas:", extractor.getImages(), "abs:src");
        }
    }

    private void writeElements(BufferedWriter writer, String title, Elements elements, String attrKey) throws IOException {
        writer.write(title);
        writer.newLine();
        elements.forEach(element -> {
            String link = element.attr(attrKey);
            boolean isSafe = LinkSafetyChecker.isLinkSafe(link);
            try {
                writer.write(link + " - " + (isSafe ? "Seguro" : "Inseguro"));
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        writer.write("---------------------------");
        writer.newLine();
    }

    private void writeValidElements(BufferedWriter writer, String title, Elements elements, String attrKey) throws IOException {
        writer.write(title);
        writer.newLine();
        elements.stream()
                .map(element -> element.attr(attrKey))
                .filter(link -> LinkValidator.getHttpStatus(link) == 200)
                .forEach(link -> {
                    boolean isSafe = LinkSafetyChecker.isLinkSafe(link);
                    try {
                        writer.write(link + " - " + (isSafe ? "Seguro" : "Inseguro"));
                        writer.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        writer.write("---------------------------");
        writer.newLine();
    }
}
