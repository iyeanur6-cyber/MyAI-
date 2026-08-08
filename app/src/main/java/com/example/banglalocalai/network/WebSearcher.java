package com.example.banglalocalai.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebSearcher {

    public static String searchWeb(String query) {
        StringBuilder result = new StringBuilder();
        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            URL url = new URL("https://html.duckduckgo.com/html/?q=" + encodedQuery);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Android; Mobile)");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder html = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                html.append(line);
            }
            reader.close();

            Pattern pattern = Pattern.compile("class=\"result__snippet\">(.*?)</a>");
            Matcher matcher = pattern.matcher(html.toString());
            int count = 0;
            while (matcher.find() && count < 2) {
                String snippet = matcher.group(1).replaceAll("<[^>]*>", "");
                result.append(snippet).append(" ");
                count++;
            }
        } catch (Exception e) {
            return "";
        }
        return result.toString().trim();
    }
}
