package org.mga44.court.vacancy;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.mga44.utils.FileWriter.writeContentsToFile;

@RequiredArgsConstructor
public class PDFCourtVacancyParser {

    private static final Logger logger = LoggerFactory.getLogger(PDFCourtVacancyParser.class);
    private final String filename;

    public String parsePDFFile() {
        logger.info("Starting operation for file {}", filename);
        try (final PDDocument document = Loader.loadPDF(new RandomAccessReadBufferedFile(filename))) {
            final PDFTextStripper pdfStripper = new PDFTextStripper();
            final String text = pdfStripper.getText(document);
            writeContentsToFile(PDFCourtVacancyParser.class, text);
            return text;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
