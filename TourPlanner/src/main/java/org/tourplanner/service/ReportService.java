package org.tourplanner.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tourplanner.persistence.entity.Tour;
import org.tourplanner.persistence.entity.TourLog;
import org.tourplanner.persistence.repository.TourLogRepository;
import org.tourplanner.service.TourMetricsCalculator;

import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final TourLogRepository logRepo;
    private final TourMetricsCalculator metrics = new TourMetricsCalculator();

    public void generateTourReport(Tour tour, Path target) throws IOException {
        List<TourLog> logs = logRepo.findByTourOrderByDate(tour);

        try(PdfWriter writer = new PdfWriter(target.toString());
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf)) {

            doc.add(new Paragraph("Tour Report – " + tour.getTourName()));
            addMetaTable(tour, doc);
            addLogsTable(logs, doc);
        }
    }

    public void generateSummaryReport(Path target) throws IOException {
        try(PdfWriter writer = new PdfWriter(target.toString());
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf)) {

            doc.add(new Paragraph("Summary Report – Placeholder"));
        }
    }

    private void addMetaTable(Tour tour, Document doc) {
        Table table = new Table(UnitValue.createPercentArray(2))
                .useAllAvailableWidth();

        table.addCell(new Cell().add(new Paragraph("From")));
        table.addCell(new Cell().add(new Paragraph(tour.getFrom())));

        table.addCell(new Cell().add(new Paragraph("To")));
        table.addCell(new Cell().add(new Paragraph(tour.getTo())));

        table.addCell(new Cell().add(new Paragraph("Distance [km]")));
        table.addCell(new Cell().add(new Paragraph(String.valueOf(tour.getDistance()))));

        table.addCell(new Cell().add(new Paragraph("Est. time [min]")));
        table.addCell(new Cell().add(new Paragraph(String.valueOf(tour.getEstimatedTime()))));

        table.addCell(new Cell().add(new Paragraph("Transport")));
        table.addCell(new Cell().add(new Paragraph(tour.getTransportType().getLabel())));

        int popularity = metrics.calculatePopularity(logRepo.findAll(), tour);
        table.addCell(new Cell().add(new Paragraph("Popularity")));
        table.addCell(new Cell().add(new Paragraph(popularity + " Stars")));

        int child = metrics.calculateChildFriendliness(logRepo.findAll(), tour);
        table.addCell(new Cell().add(new Paragraph("Child-friendly")));
        table.addCell(new Cell().add(new Paragraph(child + " Stars")));

        doc.add(table);
    }

    private void addLogsTable(List<TourLog> logs, Document doc) {
        if(logs.isEmpty()) {
            doc.add(new Paragraph("No logs available"));
            return;
        }

        Table table = new Table(UnitValue.createPercentArray(
                new float[]{2, 2, 2, 2, 2, 2, 4}))
                .useAllAvailableWidth();

        Stream.of("Date","User","Distance [km]","Time [min]","Difficulty","Rating","Comment")
                .forEach(h -> table.addHeaderCell(new Cell().add(new Paragraph(h))));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for(TourLog l : logs) {
            table.addCell(fmt.format(l.getDate()));
            table.addCell(l.getUsername());
            table.addCell(String.valueOf(l.getTotalDistance()));
            table.addCell(String.valueOf(l.getTotalTime()));
            table.addCell(l.getDifficulty().name());
            table.addCell(String.valueOf(l.getRating()));
            table.addCell(l.getComment() == null ? "" : l.getComment());
        }
        doc.add(new Paragraph("\nTour Logs"));   // small heading
        doc.add(table);
    }

}