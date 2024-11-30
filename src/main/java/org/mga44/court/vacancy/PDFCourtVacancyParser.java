package org.mga44.court.vacancy;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.util.Set;

import static org.mga44.utils.FileWriter.writeToOut;
import static org.mga44.utils.FileWriter.writeToResult;

@Slf4j
@RequiredArgsConstructor
public class PDFCourtVacancyParser implements Sequencable<String, String> {

    @Override
    public boolean enabled(Set<Step> enabled) {
        return enabled.contains(Step.PARSE);
    }

    @Override
    @SneakyThrows
    public String execute(String input) {
        log.info("Starting operation for file {}", input);
        try (final PDDocument document = Loader.loadPDF(new RandomAccessReadBufferedFile(input))) {
            final PDFTextStripper pdfStripper = new PDFTextStripper();
            final String text = pdfStripper.getText(document);
            log.info("Parsed [{}] lines from PDF", text.split(System.lineSeparator()).length);
            return text;
        }
    }

    @Override
    public void writeResult(String output) {
        writeToOut(PDFCourtVacancyParser.class, output);
        writeToResult(PDFCourtVacancyParser.class, output);
    }
}
