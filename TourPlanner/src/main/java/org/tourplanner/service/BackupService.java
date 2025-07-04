package org.tourplanner.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tourplanner.backup.TourBackupDTO;
import org.tourplanner.backup.TourLogBackupDTO;
import org.tourplanner.persistence.entity.*;
import org.tourplanner.persistence.repository.TourLogRepository;
import org.tourplanner.persistence.repository.TourRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BackupService {
    private final TourRepository tourRepo;
    private final TourLogRepository logRepo;
    private final OpenRouteServiceAgent ors;

    private final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public void exportAllTours(Path target) throws IOException {

        List<TourBackupDTO> dtoList = tourRepo.findAll().stream().map(tour -> {
            List<TourLogBackupDTO> logDTOs =
                    logRepo.findByTourOrderByDate(tour).stream().map(log ->
                            new TourLogBackupDTO(
                                    log.getDate().toString(),
                                    log.getUsername(),
                                    log.getTotalTime(),
                                    log.getTotalDistance(),
                                    log.getDifficulty().name(),
                                    log.getRating(),
                                    log.getComment())
                    ).toList();

            return new TourBackupDTO(
                    tour.getTourName(),
                    tour.getTourDescription(),
                    tour.getFrom(),
                    tour.getTo(),
                    tour.getTransportType().name(),
                    logDTOs);
        }).toList();

        mapper.writeValue(target.toFile(), dtoList);
    }

    public void importTours(Path src) throws IOException {

        TourBackupDTO[] imported = mapper.readValue(src.toFile(), TourBackupDTO[].class);

        for(TourBackupDTO dto : imported) {
            // Address -> Coordinates
            GeoCoord start = ors.geoCode(dto.from());
            GeoCoord end = ors.geoCode(dto.to());
            if(start == null || end == null) throw new IOException(
                    "Could not geocode addresses: " + dto.from() + " / " + dto.to());

            // Request route from OpenRouteServiceAgent
            OpenRouteServiceAgent.RouteType route = switch (TransportType.valueOf(dto.transportType())) {
                case DRIVING_CAR -> OpenRouteServiceAgent.RouteType.DRIVING_CAR;
                case DRIVING_HGV -> OpenRouteServiceAgent.RouteType.DRIVING_HGV;
                case CYCLING_REGULAR -> OpenRouteServiceAgent.RouteType.CYCLING_REGULAR;
                case CYCLING_ROAD -> OpenRouteServiceAgent.RouteType.CYCLING_ROAD;
                case CYCLING_MOUNTAIN -> OpenRouteServiceAgent.RouteType.CYCLING_MOUNTAIN;
                case CYCLING_ELECTRIC -> OpenRouteServiceAgent.RouteType.CYCLING_ELECTRIC;
                case FOOT_WALKING -> OpenRouteServiceAgent.RouteType.FOOT_WALKING;
                case FOOT_HIKING -> OpenRouteServiceAgent.RouteType.FOOT_HIKING;
            };

            JsonNode dir = ors.directions(route, start, end);
            if(dir == null) throw new IOException("ORS directions call failed for " + dto.tourName());

            double distanceKm = dir.at("/features/0/properties/summary/distance").asDouble() / 1000.0;
            double durationMin = dir.at("/features/0/properties/summary/duration").asDouble() / 60.0;
            String geoJson = dir.toString();

            // Create tour in DB
            Tour tour = new Tour();
            tour.setTourName(dto.tourName());
            tour.setTourDescription(dto.tourDescription());
            tour.setFrom(dto.from());
            tour.setTo(dto.to());
            tour.setTransportType(TransportType.valueOf(dto.transportType()));
            tour.setDistance((int) Math.round(distanceKm));
            tour.setEstimatedTime((int) Math.round(durationMin));
            tour.setRouteInformation(geoJson);

            Tour savedTour = tourRepo.save(tour);

            // Create logs
            for (TourLogBackupDTO logs : dto.tourLogs()) {
                TourLog log = new TourLog(
                        LocalDate.parse(logs.date()),
                        logs.username(),
                        logs.totalTime(),
                        logs.totalDistance(),
                        Difficulty.valueOf(logs.difficulty()),
                        logs.rating(),
                        logs.comment(),
                        savedTour);
                logRepo.save(log);
            }
        }
    }
}
