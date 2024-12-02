package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class LinkExtractor {
    private final String url;
    private Elements links;
    private Elements media;
    private Elements images;

    public LinkExtractor(String url) throws IOException {
        this.url = url;
        fetchContent();
    }

    private void fetchContent() throws IOException {
        Document doc = Jsoup.connect(url).get();
        links = doc.select("a[href]");
        media = doc.select("[src]");
        images = doc.select("img[src~=(?i)\\.(jpeg|jpg|png|gif|bmp|tiff|tif|svg|webp|avif|heif|ico)]");
    }

    public Elements getLinks() {
        return links;
    }

    public Elements getMedia() {
        return media;
    }

    public Elements getImages() {
        return images;
    }
}
