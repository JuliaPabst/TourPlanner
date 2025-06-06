package org.tourplanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
public class OpenRouteServiceAgent {
    private static final String API_KEY= "5b3ce3597851110001cf6248b8e1f20dec4249ed8b7c216a8d9057d5";

    private String apiKey;

    public OpenRouteServiceAgent(String apiKey) {
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

    static enum RouteType {
        CAR("driving-car");

        private final String s;

        RouteType(String s){
            this.s = s;
        }

        @Override
        public String toString() {
            return s;
        }
    }
}
