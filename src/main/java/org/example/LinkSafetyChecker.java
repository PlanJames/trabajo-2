package org.example;

import com.google.api.client.http.*;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;

import java.util.HashSet;
import java.util.Set;

public class LinkSafetyChecker {
    private static final String API_KEY = "AIzaSyDIlAiTPhKT50ZXEfm8G--xbBgbsNh4fwk";  // Reemplaza con tu clave de API
    private static final String SAFE_BROWSING_URL = "https://safebrowsing.googleapis.com/v4/threatMatches:find?key=" + API_KEY;
    private static final Set<String> checkedLinks = new HashSet<>();

    public static boolean isLinkSafe(String link) {
        if (checkedLinks.contains(link)) {
            return true;
        }

        try {
            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            String requestBody = "{\n" +
                    "  \"client\": {\n" +
                    "    \"clientId\": \"linklist\",\n" +
                    "    \"clientVersion\": \"1.0\"\n" +
                    "  },\n" +
                    "  \"threatInfo\": {\n" +
                    "    \"threatTypes\": [\"MALWARE\", \"SOCIAL_ENGINEERING\"],\n" +
                    "    \"platformTypes\": [\"ANY_PLATFORM\"],\n" +
                    "    \"threatEntryTypes\": [\"URL\"],\n" +
                    "    \"threatEntries\": [{\"url\": \"" + link + "\"}]\n" +
                    "  }\n" +
                    "}";

            HttpRequestFactory requestFactory = httpTransport.createRequestFactory(request -> request.setParser(new JsonObjectParser(jsonFactory)));
            HttpRequest request = requestFactory.buildPostRequest(new GenericUrl(SAFE_BROWSING_URL), ByteArrayContent.fromString("application/json", requestBody));

            HttpResponse response = request.execute();
            String responseString = response.parseAsString();

            checkedLinks.add(link);

            return !responseString.contains("matches");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
