package org.tourplanner.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.tourplanner.persistence.entity.GeoCoord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Service-Agent for calling the REST API of www.openrouteservice.org
 */
@Service
public class OpenRouteServiceAgent {
    private static final Logger log = LogManager.getLogger(OpenRouteServiceAgent.class);
    private final String apiKey;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public OpenRouteServiceAgent(@Value("${openroute.api.key}") String apiKey) {
        this.apiKey = apiKey;
        log.debug("OpenRouteServiceAgent initialized");
    }

    public GeoCoord geoCode(String postalAddress) {
        log.info("Geocoding address: {}", postalAddress);
        String encoded;
        encoded = URLEncoder.encode(postalAddress, StandardCharsets.UTF_8);
        String url = String.format("https://api.openrouteservice.org/geocode/search?api_key=%s&text=%s", apiKey, encoded);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.debug("Geocode HTTP status: {}", response.statusCode());

            if(response.statusCode() == 200) {
                JsonNode root = mapper.readTree(response.body());
                log.debug("Geocode response payload: {}", root.toPrettyString());

                try {
                    var coords = root.get("features").get(0).get("geometry").get("coordinates");
                    double lon = coords.get(0).asDouble();
                    double lat = coords.get(1).asDouble();
                    log.info("Parsed coordinates: lon={}, lat={}", lon, lat);
                    return new GeoCoord(lon, lat);
                } catch (Exception e){
                    log.error("Failed to parse geocode response: {}", root.toPrettyString(), e);
                    return null;
                }
            } else {
                log.error("Geocode request failed (status={}): {}", response.statusCode(), response.body());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            log.error("Exception during geocode request: {}", e.getMessage(), e);
            return null;
        }
    }

    public JsonNode directions(RouteType routeType, GeoCoord start, GeoCoord end) {
        log.info("Requesting directions: {} -> {} via {}", start, end, routeType);
        NumberFormat fmt = NumberFormat.getNumberInstance(Locale.US);
        fmt.setMaximumFractionDigits(6);

        String url = String.format("https://api.openrouteservice.org/v2/directions/%s?api_key=%s&start=%s,%s&end=%s,%s",
                routeType,
                apiKey,
                fmt.format(start.lon()), fmt.format(start.lat()),
                fmt.format(end.lon()), fmt.format(end.lat())
        );

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.debug("Directions HTTP status: {}", response.statusCode());

            if(response.statusCode() == 200) {
                JsonNode root = mapper.readTree(response.body());
                log.debug("Directions response payload: {}", root.toPrettyString());
                return root;
            } else {
                log.error("Directions request failed (status={}): {}", response.statusCode(), response.body());
                return null;
            }

        } catch (IOException | InterruptedException e) {
            log.error("Exception during directions request: {}", e.getMessage(), e);
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
