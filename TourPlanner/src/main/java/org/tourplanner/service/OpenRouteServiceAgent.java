package org.tourplanner.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.tourplanner.persistence.entity.GeoCoord;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Service-Agent for calling the REST API of www.openrouteservice.org
 */
@Service
public class OpenRouteServiceAgent {
    private final String apiKey;

    public OpenRouteServiceAgent(@Value("${openroute.api.key}") String apiKey) {
        this.apiKey = apiKey;
    }

    public GeoCoord geoCode(String postalAddress) {
        try {
            postalAddress = URLEncoder.encode(postalAddress, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.err.println("Unsupported characters in postal address:  " + e.getMessage());
            return null;
        }
        String url = String.format("https://api.openrouteservice.org/geocode/search?api_key=%s&text=%s", apiKey, postalAddress);

        try (HttpClient client = HttpClient.newHttpClient();){
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.body());

                try {
                    var coords = root.get("features").get(0).get("geometry").get("coordinates");
                    return new GeoCoord(coords.get(0).asDouble(), coords.get(1).asDouble());
                } catch (Exception e){
                    System.err.println("Failed to parse REST response " + root.toPrettyString());
                    return null;
                }
            } else {
                System.err.println("Failed to process request: " + response.body());
                return null;
            }

        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public JsonNode directions(RouteType routeType, GeoCoord start, GeoCoord end) {
        // convert double to string using . as comma
        var formatter = NumberFormat.getNumberInstance(Locale.UK);
        formatter.setMaximumFractionDigits(6);

        String url = String.format("https://api.openrouteservice.org/v2/directions/%s?api_key=%s&start=%s,%s&end=%s,%s", routeType.toString(), apiKey, formatter.format(start.lat()), formatter.format(start.lon()), formatter.format(end.lat()), formatter.format(end.lon()));

        try (HttpClient client = HttpClient.newHttpClient();){
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.body());
                System.out.println(root.toPrettyString());
                return root;
            } else {
                System.err.println("Failed to process request: " + response.body());
                return null;
            }

        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public enum RouteType {
        DRIVING_CAR("driving-car"),
        DRIVING_HGV("driving-hgv"),
        CYCLING_REGULAR("cycling-regular"),
        CYCLING_ROAD("cycling-road"),
        CYCLING_MOUNTAIN("cycling-mountain"),
        CYCLING_ELECTRIC("cycling-electric"),
        FOOT_WALKING("foot-walking"),
        FOOT_HIKING("foot-hiking");

        private final String orsName;

        RouteType(String orsName) {
            this.orsName = orsName;
        }

        @Override
        public String toString() {
            return orsName;
        }
    }
}
