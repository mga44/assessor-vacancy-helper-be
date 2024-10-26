package org.main;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.main.FileWriter.writeContentsToFile;

@RequiredArgsConstructor
public class PDFCourtVacancyParser {

    private static final Logger logger = LoggerFactory.getLogger(PDFCourtVacancyParser.class);
    private final String filename;

    public List<CourtVacancy> getVacancies() {
        logger.info("Starting operation for file {}", filename);
        final String text = getTextContent();
        writeContentsToFile(PDFCourtVacancyParser.class, text);
        final Map<String, List<String>> sanitizedLanes = new LaneSanitizer().clean(text);
        final VacancyMapper vacancyMapper = new VacancyMapper();
        return List.of();
    }

    private String getTextContent() {
        try (final PDDocument document = Loader.loadPDF(new RandomAccessReadBufferedFile(filename))) {
            final PDFTextStripper pdfStripper = new PDFTextStripper();
            final String text = pdfStripper.getText(document);
            return text;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
