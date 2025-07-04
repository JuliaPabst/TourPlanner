package org.tourplanner.service;

import jakarta.annotation.PostConstruct;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.animation.PauseTransition;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.springframework.stereotype.Service;
import org.tourplanner.persistence.entity.Tour;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class MapSnapshotService {

    private static final Logger log = LogManager.getLogger(MapSnapshotService.class);
    private Path mapDir;

    @PostConstruct
    private void init() throws IOException {
        mapDir = Paths.get("maps");
        Files.createDirectories(mapDir);
    }

    public void invalidateMapImage(Tour tour) {
        if(tour == null || tour.getTourId() == null) return;
        Path img = mapDir.resolve(tour.getTourId() + ".png");
        try {
            if(Files.deleteIfExists(img)) {
                log.debug("Outdated map image {} removed", img);
            }
        } catch(IOException ex) {
            log.error("Failed to delete old map image {} -> {}", img, ex.getMessage());
        }
    }

    public void ensureMapImage(Tour tour) {
        if(tour.getRouteInformation() == null || tour.getRouteInformation().isBlank())
            return;

        Path img = mapDir.resolve(tour.getTourId() + ".png");
        if(Files.exists(img)) return;

        CountDownLatch latch = new CountDownLatch(1);
        Runnable job = () -> createSnapshotAndSave(tour, img, latch);

        if(Platform.isFxApplicationThread())
            job.run();
        else
            Platform.runLater(job);

        try {
            boolean finished = latch.await(15, TimeUnit.SECONDS);
            if(!finished) {
                System.err.println("Map snapshot timed out");
            }
        } catch (InterruptedException ignored) {}
    }

    private void createSnapshotAndSave(Tour tour, Path img, CountDownLatch latch) {

        WebView web = new WebView();
        web.setPrefSize(800, 600);

        Scene scene = new Scene(web);
        Stage stage = new Stage(StageStyle.TRANSPARENT);   // invisible window
        stage.setScene(scene);
        stage.setOpacity(0);
        stage.setX(-2000);
        stage.setY(-2000);
        stage.show();

        web.getEngine().loadContent(HtmlUtil.wrapLeaflet(tour.getRouteInformation()), "text/html");

        web.getEngine().getLoadWorker().stateProperty().addListener((obs, ov, nv) -> {
            if(nv == Worker.State.SUCCEEDED) {
                // wait a biit for leaflet tiles
                PauseTransition wait = new PauseTransition(Duration.seconds(3));
                wait.setOnFinished(e -> {
                    BufferedImage awt = SwingFXUtils.fromFXImage(web.snapshot(null, null), null);
                    try {
                        ImageIO.write(awt, "png", img.toFile());
                    } catch(IOException ex) {
                        ex.printStackTrace();
                    } finally {
                        stage.close();      // tidy up
                        latch.countDown();  // snapshot finished
                    }
                });
                wait.playFromStart();
            }
        });
    }

    // Helper
    private static class HtmlUtil {
        private static final String TEMPLATE = """
        <!DOCTYPE html><html><head>
          <meta charset='utf-8'>
          <style>html,body,#map{height:100%%;margin:0}</style>
          <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
          <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
        </head><body><div id="map"></div>
        <script>
          var route = %s;
          var map = L.map('map');
          L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
                       { maxZoom: 19 }).addTo(map);
          var layer = L.geoJSON(route).addTo(map);
          map.fitBounds(layer.getBounds());
        
          map.whenReady(function() {
              map.invalidateSize();
          });
        </script></body></html>
        """;
        static String wrapLeaflet(String json) { return TEMPLATE.formatted(json); }
    }
}