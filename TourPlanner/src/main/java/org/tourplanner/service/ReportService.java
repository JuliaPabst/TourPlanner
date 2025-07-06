package org.tourplanner.service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.element.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.tourplanner.persistence.entity.Tour;
import org.tourplanner.persistence.entity.TourLog;
import org.tourplanner.persistence.repository.TourLogRepository;
import org.tourplanner.persistence.repository.TourRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final TourRepository tourRepo;
    private final TourLogRepository logRepo;
    private final MapSnapshotService mapSnap;
    private final TourMetricsCalculator metrics = new TourMetricsCalculator();

    public void generateTourReport(Tour tour, Path target) throws IOException {
        mapSnap.ensureMapImage(tour);
        List<TourLog> logs = logRepo.findByTourOrderByDate(tour);

        try(PdfWriter writer = new PdfWriter(target.toString());
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf)) {

            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont italic = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);

            // Title
            doc.add(new Paragraph(new Text("Tour Report").setFont(bold).setFontSize(18)));
            doc.add(new Paragraph(new Text("Tour: " + tour.getTourName()).setFont(italic).setFontSize(12)).setMarginBottom(10));

            addMetaTable(tour, doc, bold);
            addMapImage(tour, doc, bold);
            addLogsTable(logs, doc, bold);
        }
    }

    public void generateSummaryReport(Path target) throws IOException {
        List<Tour> tours = tourRepo.findAll();
        try(PdfWriter writer = new PdfWriter(target.toString());
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf)) {

            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            doc.add(new Paragraph(new Text("Summary Report").setFont(bold).setFontSize(18)).setMarginBottom(10));

            Table table = new Table(UnitValue.createPercentArray(new float[]{4,2,2,2}))
                    .useAllAvailableWidth();
            Stream.of("Tour","Ø Distance [km]","Ø Time [min]","Ø Rating")
                    .forEach(h -> table.addHeaderCell(new Cell().add(new Paragraph(h).setFont(bold))));

            for(Tour tour : tours) {
                List<TourLog> logs = logRepo.findByTourOrderByDate(tour);

                String avgDist, avgTime, avgRating;
                if(logs.isEmpty()) {
                    avgDist = avgTime = avgRating = "N/A";
                } else {
                    avgDist = String.format(Locale.US, "%.1f", logs.stream().mapToDouble(TourLog::getTotalDistance).average().orElse(0));
                    avgTime = String.format(Locale.US, "%.1f", logs.stream().mapToInt(TourLog::getTotalTime).average().orElse(0));
                    avgRating = String.format(Locale.US, "%.1f", logs.stream().mapToInt(TourLog::getRating).average().orElse(0));
                }

                table.addCell(tour.getTourName());
                table.addCell(avgDist);
                table.addCell(avgTime);
                table.addCell(avgRating);
            }
            doc.add(table);
        }
    }

    private void addMetaTable(Tour tour, Document doc, PdfFont boldFont) {
        doc.add(new Paragraph("\nTour Details").setFont(boldFont).setFontSize(14).setMarginTop(10));

        Table table = new Table(UnitValue.createPercentArray(2))
                .useAllAvailableWidth();

        table.addCell(new Cell().add(new Paragraph("From").setFont(boldFont)));
        table.addCell(new Cell().add(new Paragraph(tour.getFrom())));

        table.addCell(new Cell().add(new Paragraph("To").setFont(boldFont)));
        table.addCell(new Cell().add(new Paragraph(tour.getTo())));

        table.addCell(new Cell().add(new Paragraph("Distance [km]").setFont(boldFont)));
        table.addCell(new Cell().add(new Paragraph(String.valueOf(tour.getDistance()))));

        table.addCell(new Cell().add(new Paragraph("Est. time [min]").setFont(boldFont)));
        table.addCell(new Cell().add(new Paragraph(String.valueOf(tour.getEstimatedTime()))));

        table.addCell(new Cell().add(new Paragraph("Transport").setFont(boldFont)));
        table.addCell(new Cell().add(new Paragraph(tour.getTransportType().getLabel())));

        int popularity = metrics.calculatePopularity(logRepo.findAll(), tour);
        table.addCell(new Cell().add(new Paragraph("Popularity").setFont(boldFont)));
        table.addCell(new Cell().add(new Paragraph(popularity + " Stars")));

        int child = metrics.calculateChildFriendliness(logRepo.findAll(), tour);
        table.addCell(new Cell().add(new Paragraph("Child-friendly").setFont(boldFont)));
        table.addCell(new Cell().add(new Paragraph(child + " Stars")));

        doc.add(table);
    }

    private void addLogsTable(List<TourLog> logs, Document doc, PdfFont boldFont) {
        doc.add(new Paragraph("\nTour Logs").setFont(boldFont).setFontSize(14).setMarginTop(10));

        if(logs.isEmpty()) {
            doc.add(new Paragraph("No logs available"));
            return;
        }

        Table table = new Table(UnitValue.createPercentArray(
                new float[]{2, 2, 2, 2, 2, 2, 4}))
                .useAllAvailableWidth();

        Stream.of("Date","User","Distance [km]","Time [min]","Difficulty","Rating","Comment")
                .forEach(h -> table.addHeaderCell(new Cell().add(new Paragraph(h).setFont(boldFont))));

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

        doc.add(table);
    }

    private void addMapImage(Tour tour, Document doc, PdfFont boldFont) throws IOException {
        doc.add(new Paragraph("\nMap").setFont(boldFont).setFontSize(14).setMarginTop(10));

        Path img = Path.of("maps", tour.getTourId() + ".png"); // e.g. maps/3.png

        if(Files.exists(img)) {
            ImageData data = ImageDataFactory.create(img.toUri().toString()); // Convert Path to URI String
            Image map = new Image(data).scaleToFit(400, 300);
            doc.add(map);
        } else {
            doc.add(new Paragraph("\nMap image not available"));
        }
    }

}