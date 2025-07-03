package org.tourplanner.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tourplanner.backup.TourBackupDTO;
import org.tourplanner.backup.TourLogBackupDTO;
import org.tourplanner.persistence.repository.TourLogRepository;
import org.tourplanner.persistence.repository.TourRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BackupService {
    private final TourRepository tourRepo;
    private final TourLogRepository logRepo;
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
}
