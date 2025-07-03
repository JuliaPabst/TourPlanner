package org.tourplanner.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;
import org.tourplanner.persistence.entity.Tour;

import java.io.IOException;
import java.nio.file.Path;

@Service
public class ReportService {

    public void generateTourReport(Tour tour, Path target) throws IOException {
        try(PdfWriter writer = new PdfWriter(target.toString());
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf)) {

            doc.add(new Paragraph("Tour Report – " + tour.getTourName()));
            addMetaTable(tour, doc);
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

        doc.add(table);
    }
}