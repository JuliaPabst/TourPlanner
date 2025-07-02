package org.tourplanner.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

@Service
public class ReportService {

    // TODO: No tour parameters yet, just for testing PDF creation
    public void generateTourReport(Path target) throws IOException {
        try(PdfWriter writer = new PdfWriter(target.toString());
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf)) {

            doc.add(new Paragraph("Tour Report – Placeholder"));
        }
    }

    public void generateSummaryReport(Path target) throws IOException {
        try(PdfWriter writer = new PdfWriter(target.toString());
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf)) {

            doc.add(new Paragraph("Summary Report – Placeholder"));
        }
    }
}