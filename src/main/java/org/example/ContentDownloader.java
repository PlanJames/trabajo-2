package org.example;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ContentDownloader {

    private static final String MEDIA_FOLDER = "src/main/resources/media/";
    private static final String PDF_OUTPUT = "src/main/resources/images.pdf";

    public void downloadContent(LinkExtractor extractor) {
        try {
            Files.createDirectories(Paths.get(MEDIA_FOLDER));

            generatePdfWithImages(extractor.getImages());

        } catch (IOException e) {
            System.err.println("Error al descargar el contenido: " + e.getMessage());
        }
    }

    private void generatePdfWithImages(Elements images) throws IOException {
        PdfWriter pdfWriter = new PdfWriter(PDF_OUTPUT);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument);

        Paragraph header = new Paragraph("Imágenes")
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(18);
        document.add(header);
        document.add(new Paragraph("\n"));

        for (String imageUrl : extractUrls(images, "abs:src")) {
            try {
                String imagePath = downloadImage(imageUrl);
                if (imagePath != null) {
                    Image img = new Image(com.itextpdf.io.image.ImageDataFactory.create(imagePath));
                    document.add(img);
                    document.add(new Paragraph("\n"));
                }
            } catch (Exception e) {
                System.err.println("Error al descargar la imagen: " + imageUrl + " - " + e.getMessage());
            }
        }

        document.close();
        System.out.println("Imágenes guardadas en " + PDF_OUTPUT);
    }

    private String downloadImage(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            String imageName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            String imagePath = MEDIA_FOLDER + imageName;

            Files.createDirectories(Paths.get(MEDIA_FOLDER));

            try (InputStream in = url.openStream()) {
                Files.copy(in, Paths.get(imagePath));
                return imagePath;
            }
        } catch (IOException e) {
            System.err.println("Error al descargar la imagen: " + imageUrl);
            return null;
        }
    }

    private List<String> extractUrls(Elements elements, String attrKey) {
        return elements.stream()
                .map(element -> element.attr(attrKey))
                .collect(Collectors.toList());
    }
}