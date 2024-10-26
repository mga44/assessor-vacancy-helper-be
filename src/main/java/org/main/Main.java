package org.main;

import org.mga44.court.vacancy.CourtVacancy;
import org.mga44.court.vacancy.LaneSanitizer;
import org.mga44.court.vacancy.PDFCourtVacancyParser;
import org.mga44.court.vacancy.VacancyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String FILENAME_1 = "C:\\Users\\marku\\Documents\\GitHub\\lox\\AssessPlaceHelper\\src\\main\\resources\\zal._nr_1_zarzadzenie_ministra_sprawiedliwosci.pdf";
    private static final String FILENAME_2 = "C:\\Users\\marku\\Documents\\GitHub\\lox\\AssessPlaceHelper\\src\\main\\resources\\zarzadzenie_ms_z_26.04.24_-_wykaz_wolnych_stanowisk_asesorskich.pdf";
    private static final String FILENAME_3 = "C:\\Users\\marku\\Documents\\GitHub\\lox\\AssessPlaceHelper\\src\\main\\resources\\2024-wyniki.pdf";

    public static void main(String[] args) {
        final PDFCourtVacancyParser parser = new PDFCourtVacancyParser(FILENAME_1);
        String pdfTextContents = parser.parsePDFFile();
        final Map<String, List<String>> sanitizedLanes = new LaneSanitizer().clean(pdfTextContents);
        final VacancyMapper vacancyMapper = new VacancyMapper();
        List<CourtVacancy> vacancies = vacancyMapper.mapToVacancies(sanitizedLanes);
        logger.info("Found {} vacancies", vacancies.size());
    }
}