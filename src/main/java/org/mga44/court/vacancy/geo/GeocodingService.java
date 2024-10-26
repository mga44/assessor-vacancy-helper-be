package org.mga44.court.vacancy.geo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class GeocodingService {
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";

    public Optional<Coordinates> getCoordinates(String placeName) {
        String response = makeAPIRequest(placeName);
        if (response == null) {
            return Optional.empty();
        }

        // Parse JSON response
        Type listType = new TypeToken<ArrayList<Coordinates>>() {
        }.getType();
        List<Coordinates> coordinates = new Gson().fromJson(response, listType);
        if (!coordinates.isEmpty()) {
            return Optional.of(coordinates.get(0));
        } else {
            log.warn("Not found place: [{}]", placeName);
            return Optional.empty(); // No results found
        }
    }


    private String makeAPIRequest(String placeName) {
        try {
            // Construct the API URL with query parameters
            String urlStr = NOMINATIM_URL + "?q=" + URLEncoder.encode(placeName, StandardCharsets.UTF_8) + "&format=json&limit=1";
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0"); // Required by Nominatim

            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } catch (Exception e) {
            log.error("Could not make API call: ", e.getCause());
            return null;
        }
    }
}
